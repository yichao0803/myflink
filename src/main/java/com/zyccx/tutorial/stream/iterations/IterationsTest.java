package com.zyccx.tutorial.stream.iterations;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Iterations 迭代  测试
 */
public class IterationsTest {

    public void run(String[] args) throws Exception {

        ParameterTool param = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(param);
        env.setParallelism(1);


        DataStream<Long> someIntegers = env.generateSequence(0, 5);

        someIntegers.writeAsText("log\\someIntegers.txt");
        IterativeStream<Long> iteration = someIntegers.iterate();

        DataStream<Long> minusOne = iteration.map(new MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return value - 1;
            }
        });

        minusOne.writeAsText("log\\minusOne.txt");


        DataStream<Long> stillGreaterThanZero = minusOne.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                return (value > 0);
            }
        });

        iteration.closeWith(stillGreaterThanZero);

        DataStream<Long> lessThanZero = minusOne.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {

                return (value <= 0);
            }
        });
        lessThanZero.writeAsText("log\\lessThanZero.txt");
        env.execute();

//        ParameterTool param=ParameterTool.fromArgs(args);
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.getConfig().setGlobalJobParameters(param);
//
//        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
//
//
//        IterativeStream<Tuple3<String, Integer, Integer>> iterate = source.iterate();
//
//
//
//        source.print();
//        env.execute("IterationsTest");

    }

    public static void main(String[] args) {
        IterationsTest iterationsTest = new IterationsTest();
        try {
            iterationsTest.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
