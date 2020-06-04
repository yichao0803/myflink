package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * KeyBy:从逻辑上将流划分为不相交的分区。具有相同键的所有记录都分配给同一分区。在内部，keyBy（）是通过哈希分区实现的。有多种指定密钥的方法。
 * <p>
 * KeyByAndSumTest
 * 参考资料：https://blog.csdn.net/wangpei1949/article/details/101625394
 */
public class KeyByAndSumTest {

    /**
     * getSource
     *
     * @return
     */
    public static List<Tuple3<String, Integer, Integer>> getSource() {
        List<Tuple3<String, Integer, Integer>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new Tuple3<>(i % 2 == 0 ? "A" : "B", 5 - i, i));
        }
        return list;
    }

    /**
     * main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(getSource());
        KeyedStream<Tuple3<String, Integer, Integer>, Tuple> keyedStream = source.keyBy(0);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> sum = keyedStream.sum(1);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> min = keyedStream.min(1);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> minBy = keyedStream.minBy(1);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> reduce = keyedStream.reduce(new ReduceFunction<Tuple3<String, Integer, Integer>>() {
            @Override
            public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> value1, Tuple3<String, Integer, Integer> value2) throws Exception {
                return new Tuple3<>(value1.f0, value1.f1 + value2.f1, value1.f2);
            }
        });

        // sum.print("sum：");
        min.print("min：");
        minBy.print("minBy：");
        //reduce.print("reduce：");

        env.execute(" key by and sum test.");
    }

}
