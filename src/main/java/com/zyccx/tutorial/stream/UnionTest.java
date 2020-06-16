package com.zyccx.tutorial.stream;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class UnionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        DataStreamSource<Tuple3<String, Integer, Integer>> sourceSameValue = env.fromCollection(KeyByData.getSourceSameValue());
        DataStream<Tuple3<String, Integer, Integer>> union = source.union(sourceSameValue);

        union.print("union");
        env.execute("UnionTest");
    }
}
