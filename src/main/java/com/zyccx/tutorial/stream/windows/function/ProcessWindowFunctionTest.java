package com.zyccx.tutorial.stream.windows.function;

import com.zyccx.tutorial.stream.WatermarksTest;
import com.zyccx.tutorial.stream.windows.TumblingEventTimeWindowsTest;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple6;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试全量窗口函数 ProcessWindowFunction
 */
public class ProcessWindowFunctionTest {

    /**
     * 通过  DataStream API  指定 watermarks
     *
     * @throws Exception
     */
    private static void assignAscendingTimestampsPunctuatedTest() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Tuple3<String, Long, Integer>> source = env.fromElements(Tuple3.of("a", 1L, 1000), Tuple3.of("b", 1L, 1000), Tuple3.of("a", 3L, 1100), Tuple3.of("b", 3L, 1200), Tuple3.of("c", 3L, 1300));

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> operator = source.assignTimestampsAndWatermarks(new WatermarksTest.PunctuatedAssigner());
        SingleOutputStreamOperator<Tuple6<String, Long, Long, Long, Double, Long>> process = operator
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .process(new StatusProcessWindowFunction());
        source.print("ProcessWindowFunctionTest-source");
        process.print("ProcessWindowFunctionTest-process");
        env.execute("ProcessWindowFunctionTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }

    /**
     * 返回窗口内元素 key，f1的和、最大值、最小值、平均值，窗口结束时间
     */
    public static class StatusProcessWindowFunction extends ProcessWindowFunction<Tuple3<String, Long, Integer>, Tuple6<String, Long, Long, Long, Double, Long>, Tuple, TimeWindow>{

        @Override
        public void process(Tuple key, Context context, Iterable<Tuple3<String, Long, Integer>> elements,
                            Collector<Tuple6<String, Long, Long, Long, Double, Long>> out) throws Exception {
            List<Tuple3<String, Long, Integer>> list = new ArrayList<>();
            elements.forEach(a -> list.add(a));

            Long sum = list.stream().mapToLong(a -> a.f1).sum();
            Long max = list.stream().mapToLong(a -> a.f1).max().getAsLong();
            Long min = list.stream().mapToLong(a -> a.f1).min().getAsLong();
            Double avg = list.stream().mapToLong(a -> a.f1).average().getAsDouble();
            Long windowsEnd = context.window().getEnd();

            out.collect(Tuple6.of(key.getField(0).toString(), sum, max, min, avg, windowsEnd));
        }
    }

}
