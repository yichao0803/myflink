package com.zyccx.tutorial.source;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取 kafka 指定分区的数据
 */
public class KafkaPartitionTest {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(4000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.100.80:9092");
        properties.setProperty("zookeeper.connect", "192.168.100.80:2181");
        properties.setProperty("group.id", "test-01");

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>(
                "9957467286a44449821016030f2f45d2-expression-data",
                new SimpleStringSchema(),
                properties);

        // 设置
        Map<KafkaTopicPartition, Long> specificStartOffsets = new HashMap<>();
        specificStartOffsets.put(new KafkaTopicPartition(
                "9957467286a44449821016030f2f45d2-expression-data",
                2),
                0L);

        flinkKafkaConsumer.setStartFromSpecificOffsets(specificStartOffsets);

        DataStreamSource<String> topic = env.addSource(flinkKafkaConsumer);

        topic.print();

        env.execute("kafka test.");
    }
}
