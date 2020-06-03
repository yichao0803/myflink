package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * Map：取一个元素并产生一个元素。一个映射函数
 * MapTest：使用 Map 函数将输入元素值 * 2，并打印输出
 */
public class MapTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Integer> dataStream = env.fromElements(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        SingleOutputStreamOperator<Integer> outputStreamOperator =
                dataStream.map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer value) throws Exception {
                return value * 2;
            }

        });
        outputStreamOperator.print();
        env.execute("Map Test");
    }

}
