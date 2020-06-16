package com.zyccx.tutorial.stream;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * 测试 ConnectedStreams CoFlatMap 实现了不同类的数据流的链接
 */


public class ConnectedStreamsTest2 {
    public static void main(String[] args) throws Exception {
        ParameterTool param = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(param.getConfiguration());
        DataStreamSource<Integer> source1 = env.fromElements(1, 0, 3, 4, 5, 6);
        DataStreamSource<String> source2 = env.fromElements("LOW LOW", "HIGH HIGH");

        ConnectedStreams<Integer, String> connectedStreams = source1.connect(source2);
        SingleOutputStreamOperator<String> outputStreamOperator = connectedStreams.flatMap(new CoFlatMapFunction<Integer, String, String>() {

            @Override
            public void flatMap1(Integer value, Collector<String> out) throws Exception {
                out.collect("" + value.toString());
            }

            @Override
            public void flatMap2(String value, Collector<String> out) throws Exception {
                for (String word : value.split(" ")) {
                    out.collect(word);
                }
            }
        });
        outputStreamOperator.print("conned");
        env.execute("connected stream test 2 ");

    }

}
