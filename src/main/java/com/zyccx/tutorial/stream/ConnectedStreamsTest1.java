package com.zyccx.tutorial.stream;

import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

/**
 * 本示例，中 connect map 实现了不同类的数据流的链接
 */
public class ConnectedStreamsTest1 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> intStream = env.fromElements(1, 0, 9, 2, 3, 6);
        DataStreamSource<String> stringStream = env.fromElements("LOW", "HIGH", "LOW", "LOW");
        ConnectedStreams<Integer, String> connect = intStream.connect(stringStream);
        SingleOutputStreamOperator<String> map = connect.map(new CoMapFunction<Integer, String, String>() {
            @Override
            public String map1(Integer value) throws Exception {
                return value.toString();
            }

            @Override
            public String map2(String value) throws Exception {
                return value;
            }
        });

        map.print("map");
        env.execute("connected stream test 1");
    }

}
