package com.zyccx.tutorial.stream;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试  DataStream → SplitStream
 *      SplitStream → DataStream
 */
public class SplitTest {
    public static void main(String[] args) throws Exception {
        ParameterTool param=ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(param);

        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        SplitStream<Tuple3<String, Integer, Integer>> split = source.split(new OutputSelector<Tuple3<String, Integer, Integer>>() {
            @Override
            public Iterable<String> select(Tuple3<String, Integer, Integer> value) {
                List<String> output = new ArrayList<>();
                if(value.f1%2==0){
                    output.add("even");
                }else {
                    output.add("odd");
                }
                return  output;
            }
        });
        DataStream<Tuple3<String, Integer, Integer>> even = split.select("even");
        DataStream<Tuple3<String, Integer, Integer>> odd = split.select("odd");
        DataStream<Tuple3<String, Integer, Integer>> all = split.select("even", "odd");

        even.print("even");
        odd.print("odd");
        all.print("all");

        env.execute(" split and select ");
    }
}
