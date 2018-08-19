package com.shapira.examples.stream.sentry.processor;

import com.shapira.examples.stream.sentry.model.AggreStats;
import com.shapira.examples.stream.sentry.model.MoveWindow;
import com.shapira.examples.stream.sentry.model.SourceEvent;
import com.shapira.examples.stream.sentry.serde.AggreStatsSerde;
import com.shapira.examples.stream.sentry.serde.MoveWindowSerde;
import com.shapira.examples.stream.sentry.serde.SourceEventSerde;
import com.shapira.examples.streams.stockstats.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.util.Properties;

/**
 * @desc:
 * @author: panqiong
 * @date: 2018/8/12
 */
public class TestProcessor {


    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "demoapp");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BROKER);
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, SourceEventSerde.class.getName());

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // work-around for an issue around timing of creating internal topics
        // this was resolved in 0.10.2.0 and above
        // don't use in large production apps - this increases network load
        // props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, 500);

        KStreamBuilder builder = new KStreamBuilder();

        //StreamsBuilder builder = new StreamsBuilder();

        KStream<String, SourceEvent> source = builder.stream("source_event_topic");

        // 时间窗口作为主键 TickerWindow聚合结果作为值
        // 它会确保事件流按照记录的键进行分区。因为在写数据时使用了键 ，
        // 而且在调用 groupByKey() 方法之前不会对键进行修改，数据仍 然是按照它们的键进行分区 的 ， 所以说这个方法不会做任何事情
        KStream<MoveWindow, AggreStats> statsKStream = source.groupByKey()
                .aggregate(
                        AggreStats::new,                         // 这个方法的第二个参数是一个新的对象，用于存放聚合的结果
                        (k, v, reduceStats) -> reduceStats.add(v), // add方怯用于更新窗口内的最低价格、 交易数量和总价 。
                        TimeWindows.of(5000).advanceBy(1000),    // 定义了 5s (5000ms)的时间窗口，井且每秒钟都会向前滑动。
                        new AggreStatsSerde(),                   // 序列化和反序列化结果
                        "test-stats-store"        // 参数就是本地状态存储的名字
                )//聚合结果是一个表，使用时间窗口作为主键、聚合结果作为值。它表示一条记录，以及从变更流中计算得出的特定状态
                //将表重新转成事件流，不过不再使用整个时间窗口作为键，而是 //使用一个包含了股票信息和时间窗口起始时间的对象。toStream方法将表转成一个流，
                //并将键转成TickerWindow 对象
                .toStream((key, value) -> new MoveWindow(key.key(),101,key.window().start(),key.window().end()))
                .mapValues((reduceStats) -> reduceStats.computePercent()); //最后一步是更新平均价格。现在，聚合结果里包含了总价和交易数量。 遍历所有的记
        // 录 ，井使用现有的统计信息计算平均价格

        statsKStream.to(new MoveWindowSerde(), new AggreStatsSerde(),  "stats_out_topic");

        KafkaStreams streams = new KafkaStreams(builder, props);

        streams.cleanUp();

        streams.start();

        // usually the stream application would be running forever,
        // in this example we just let it run for some time and stop since the input data is finite.
        //Thread.sleep(60000L);

        //streams.close();

    }
}
