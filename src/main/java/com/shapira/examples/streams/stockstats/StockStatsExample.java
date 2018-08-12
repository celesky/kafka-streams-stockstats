package com.shapira.examples.streams.stockstats;

import com.shapira.examples.streams.stockstats.serde.JsonDeserializer;
import com.shapira.examples.streams.stockstats.serde.JsonSerializer;
import com.shapira.examples.streams.stockstats.serde.WrapperSerde;
import com.shapira.examples.streams.stockstats.model.TickerWindow;
import com.shapira.examples.streams.stockstats.model.Trade;
import com.shapira.examples.streams.stockstats.model.TradeStats;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.util.Properties;

/**
 * Input is a stream of trades
 * Output is two streams: One with minimum and avg "ASK" price for every 10 seconds window
 * Another with the top-3 stocks with lowest minimum ask every minute
 */
public class StockStatsExample {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stockstat");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BROKER);
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, TradeSerde.class.getName());

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

        KStream<String, Trade> source = builder.stream(Constants.STOCK_TOPIC);

        // 时间窗口作为主键 聚合结果作为值
        KStream<TickerWindow, TradeStats> stats = source.groupByKey()  //它会确保事件流按照记录的键进行分区。因 为 在写数据时使用了键 ， 而且在调用 groupByKey() 方法之前不会对键进行修改，数据仍 然是按照它们的键进行分区 的 ， 所以说这个方法不会做任何事情
                .aggregate(
                        TradeStats::new,                         // 这个方法的第二个参数是一个新的对象，用于存放聚合的结果
                        (k, v, tradestats) -> tradestats.add(v), // add方怯用于更新窗口内的最低价格、 交易数量和总价 。
                        TimeWindows.of(5000).advanceBy(1000),    // 定义了 5s (5000ms)的时间窗口，井且每秒钟都会向前滑动。
                        new TradeStatsSerde(),                   // 序列化和反序列化结果
                    "trade-stats-store"        // 参数就是本地状态存储的名字，它可以是任意具有唯一性 的名字。
                )//聚合结果是一个表，包含了股票信息，并使用时间窗口作为主键、聚合结果作为值。它表示一条记录，以及从变更流中计算得出的特定状态

                .toStream((key, value) -> new TickerWindow(key.key(), key.window().start())) //将表重新转成事件流，不过不再使用整个时间窗口作为键，而是
                                                                        //使用一个包含了股票信息和时间窗口起始时间的对象。toStream方法将表转成一个流，
                                                                         //并将键转成TickerWindow 对象
                .mapValues((trade) -> trade.computeAvgPrice()); //最后一步是更新平均价格。现在，聚合结果里包含了总价和交易数量。 遍历所有的记
                                                                // 录 ，井使用现有的统计信息计算平均价格，然后把它写到输出流里。

        stats.to(new TickerWindowSerde(), new TradeStatsSerde(),  "stockstats-output");

        KafkaStreams streams = new KafkaStreams(builder, props);

        streams.cleanUp();

        streams.start();

        // usually the stream application would be running forever,
        // in this example we just let it run for some time and stop since the input data is finite.
        //Thread.sleep(60000L);

        //streams.close();

    }

    static public final class TradeSerde extends WrapperSerde<Trade> {
        public TradeSerde() {
            super(new JsonSerializer<Trade>(), new JsonDeserializer<Trade>(Trade.class));
        }
    }

    static public final class TradeStatsSerde extends WrapperSerde<TradeStats> {
        public TradeStatsSerde() {
            super(new JsonSerializer<TradeStats>(), new JsonDeserializer<TradeStats>(TradeStats.class));
        }
    }

    static public final class TickerWindowSerde extends WrapperSerde<TickerWindow> {
        public TickerWindowSerde() {
            super(new JsonSerializer<TickerWindow>(), new JsonDeserializer<TickerWindow>(TickerWindow.class));
        }
    }

}
