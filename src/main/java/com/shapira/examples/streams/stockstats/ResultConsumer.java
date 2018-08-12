package com.shapira.examples.streams.stockstats;

import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class ResultConsumer {


    /**
     * 自动提交偏移量
     */
    public void autoCommit(){
        Properties props = new Properties();
        props.put("bootstrap.servers", Constants.BROKER);
        props.put("group.id", "group1");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("stockstats-output"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records){
                //System.out.printf(new Date()+">> offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                //System.out.println(new Date()+">> record.toString() = " + record.toString());
                String key = record.key();
                KeyObj map = new Gson().fromJson(key, KeyObj.class);
                System.out.println(new Date(map.getTimestamp())+" "+ record.toString());

            }
        }
    }

    public static void main(String[] args) {
        new ResultConsumer().autoCommit();
    }

    class KeyObj{
        String ticker;
        Long timestamp;

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
