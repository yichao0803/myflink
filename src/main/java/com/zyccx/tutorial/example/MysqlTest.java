package com.zyccx.tutorial.example;

import com.zyccx.tutorial.source.JdbcReader;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * TODO
 *
 * @author by Zhangyichao
 * @date 2020/1/6 16:09
 * @see MysqlTest
 */
public class MysqlTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env= StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        DataStream dataStream=env.addSource(new JdbcReader());
        dataStream.print();
        env.execute("flink connected mysql");
    }
}
