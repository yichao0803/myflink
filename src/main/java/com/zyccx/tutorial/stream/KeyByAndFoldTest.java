package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyByAndFoldTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        KeyedStream<Tuple3<String, Integer, Integer>, Tuple> keyedStream = source.keyBy(0);
        SingleOutputStreamOperator<String> fold = keyedStream.fold("start", new FoldFunction<Tuple3<String, Integer, Integer>, String>() {
            @Override
            public String fold(String current, Tuple3<String, Integer, Integer> value) throws Exception {
                return current + "-" + value.f1;
            }
        });

        fold.print("fold: ");

        env.execute("KeyByAndFoldTest：");
    }
}
