package com.zyccx.tutorial.stream.windows;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class EventTImeSessionWindowsTest {

    /**
     * 通过  DataStream API  指定 watermarks
     *
     * @throws Exception
     */
    private static void assignAscendingTimestampsPunctuatedTest() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Tuple3<String, Long, Integer>> source = env.fromElements(
                Tuple3.of("a", 1L, 1000)
                , Tuple3.of("b", 1L, 1000)
                , Tuple3.of("a", 3L, 1100)
                , Tuple3.of("b", 3L, 1200)
                , Tuple3.of("c", 3L, 1300)
                , Tuple3.of("a", 3L, 1300)
                , Tuple3.of("b", 3L, 2300)
                , Tuple3.of("c", 3L, 3300)
                , Tuple3.of("c", 3L, 3400)
                , Tuple3.of("a", 3L, 4300)
                , Tuple3.of("c", 3L, 5300)
        );

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> operator = source.assignTimestampsAndWatermarks(new WatermarksTest.PunctuatedAssigner());

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = operator
                .keyBy(0)
                .window(EventTimeSessionWindows.withGap(Time.seconds(2)))
                .process(new TumblingEventTimeWindowsTest.CustomerProcessWindowFunction());
        // watermark: 292278994-08-17 15:12:55.807,9223372036854775807
        source.print("PunctuatedT-source");
        sum.print("PunctuatedT-sum");

        env.execute("assignAscendingTimestampsTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }
}
