package com.zyccx.tutorial.stream;

import com.zyccx.tutorial.stream.function.MyKeyBySelector;
import com.zyccx.tutorial.stream.function.MyReduce;
import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class KeyByAndReduceTest {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        KeyedStream<Tuple3<String, Integer, Integer>, String> keyedStream = source.keyBy(new MyKeyBySelector());
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> reduce = keyedStream.reduce(new MyReduce());

//        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> reduce = keyedStream.reduce(new ReduceFunction<Tuple3<String, Integer, Integer>>() {
//            @Override
//            public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> t1,
//                                                           Tuple3<String, Integer, Integer> t2) throws Exception {
//                return new Tuple3(t1.f0, t1.f1 + t2.f1, t1.f2);
//            }
//        });
        reduce.print();
        env.execute(" keyby and reduce test");

    }


}
