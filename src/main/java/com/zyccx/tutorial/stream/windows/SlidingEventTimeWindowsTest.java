package com.zyccx.tutorial.stream.windows;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class SlidingEventTimeWindowsTest {
    /**
     * 在 SourceFunction 中生成* watermark
     */
    private static void sourceFunctionEmitWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<Tuple3<String, Long, Integer>> dssStr = env.addSource(new WatermarksTest.CustomerSource(200,false));
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = dssStr
                .keyBy(0)
                .window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(2)))
                .process(new TumblingEventTimeWindowsTest.CustomerProcessWindowFunction());

        sum.print("sourceFunctionEmitWatermark-sum");

        env.execute();
    }

    public static void main(String[] args) throws Exception {
        sourceFunctionEmitWatermark();
    }
}
