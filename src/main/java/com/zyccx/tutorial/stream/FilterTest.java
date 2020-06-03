package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * filter: 为每个元素评估一个布尔函数，并保留该函数返回true的布尔函数。
 * 本示例：一个筛选出，不为零的元素
 */
public class FilterTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> source = env.fromElements(100, 80, 0, 99, 3, -1, 0, 9, 11, 44, 44, 22, 0, 1);
        SingleOutputStreamOperator<Integer> filter = source.filter(new FilterFunction<Integer>() {
            @Override
            public boolean filter(Integer value) throws Exception {
                return value != 0;
            }
        });
        filter.print();
        env.execute(" filter test");
    }
}
