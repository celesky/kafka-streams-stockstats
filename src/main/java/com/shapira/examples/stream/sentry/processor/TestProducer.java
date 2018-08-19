package com.shapira.examples.stream.sentry.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shapira.examples.stream.sentry.model.SourceEvent;
import com.shapira.examples.streams.stockstats.Constants;
import com.shapira.examples.streams.stockstats.model.Trade;
import com.shapira.examples.streams.stockstats.serde.JsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @desc: 事件处理器
 * @author: panqiong
 * @date: 2018/8/12
 */
public class TestProducer {

    public static KafkaProducer<String, SourceEvent> producer = null;

    public static void main(String[] args) throws Exception {

        System.out.println("Press CTRL-C to stop generating data");


        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting Down");
                if (producer != null)
                    producer.close();
            }
        });
        ObjectMapper mapper = new ObjectMapper();
        JsonSerializer<SourceEvent> sourceEventSerde = new JsonSerializer<>();

        //JsonSerializer<SourceEvent> eventSerializer = new JsonSerializer<>();

        // Configuring producer
        Properties props = new Properties();

        props.put("bootstrap.servers", Constants.BROKER);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", tradeSerializer.getClass().getName());
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, sourceEventSerde.getClass().getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, sourceEventSerde.getClass().getName());
        // Starting producer
        producer = new KafkaProducer<>(props);


        // initialize

        Random random = new Random();
        long iter = 0;

        Map<String, Integer> prices = new HashMap<>();

        for (String ticker : Constants.TICKERS)
            prices.put(ticker, Constants.START_PRICE);

        // Start generating events, stop when CTRL-C

        while (true) {
            iter++;
            for (String planId : Constants.plans) {

                int count = (int) (Math.random() * 10);
                int index = (int) (Math.random() * Constants.indecators.length);
                String indecator = Constants.indecators[index];
                SourceEvent event = new SourceEvent(planId,indecator,Instant.now().toEpochMilli(),count);

                // Note that we are using ticker as the key - so all asks for same stock will be in same partition
                ProducerRecord<String, SourceEvent> record = new ProducerRecord<>("source_event_topic", planId, event);

                producer.send(record, (RecordMetadata r, Exception e) -> {
                    System.out.println("send ticker success:" + event.toString());
                    if (e != null) {
                        System.out.println("Error producing to topic " + r.topic());
                        e.printStackTrace();
                    }
                });
            }
            Thread.sleep(Constants.DELAY);
        }
    }


}
