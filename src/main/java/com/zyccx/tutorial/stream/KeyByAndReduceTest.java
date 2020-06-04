package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyByAndReduceTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByAndSumTest.getSource());
        KeyedStream<Tuple3<String, Integer, Integer>, Tuple> keyedStream = source.keyBy(0);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> reduce = keyedStream.reduce(new ReduceFunction<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> t1,
                                                           Tuple3<String, Integer, Integer> t2) throws Exception {
                return new Tuple3(t1.f0, t1.f1 + t2.f1, t1.f2);
            }
        });
        reduce.print();

        env.execute(" keyby and reduce test");

    }
}
