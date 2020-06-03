package com.zyccx.tutorial.stream;


import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * FlatMap: 获取一个元素并产生零个或者多个元素
 * FlatMapTest：本例为，将句子拆分为单词
 */
public class FlatMapTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> source = env.fromElements( "To be, or not to be,--that is the question:--",
                "Whether 'tis nobler in the mind to suffer");
        DataStream<String> stream = source.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                for (String word : value.split(" ")) {
                    out.collect(word);
                }
            }
        });
        stream.print();
        env.execute("Flat Map Test");
    }
}
