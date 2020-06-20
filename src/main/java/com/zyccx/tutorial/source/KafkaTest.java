package com.zyccx.tutorial.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * 读取 kafka 的数据
 */
public class KafkaTest {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.100.80:9092");
        properties.setProperty("zookeeper.connect", "192.168.100.80:2181");
        properties.setProperty("group.id", "test-01");
        properties.setProperty("auto.offset.reset","earliest");// 设置从起始位置消费

        DataStreamSource<String> topic = env.addSource(
                new FlinkKafkaConsumer<>("9957467286a44449821016030f2f45d2-expression-data",
                        new SimpleStringSchema(), properties));

        topic.print();

        env.execute("kafka test.");

    }
}
