package com.zyccx.tutorial.stream.windows;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;


public class TumblingEventTimeWindowsTest {

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

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = operator
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .process(new CustomerProcessWindowFunction());

        source.print("PunctuatedT-source");
        sum.print("PunctuatedT-sum");

        env.execute("assignAscendingTimestampsTest");
    }

    /**
     * 第一种，**在 SourceFunction 中生成* watermark
     */
    private static void sourceFunctionEmitWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<Tuple3<String, Long, Integer>> dssStr = env.addSource(new WatermarksTest.CustomerSource());
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = dssStr
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(7)))
                .apply(new WatermarksTest.CustomerWindowFunctionApply());

        sum.print("sourceFunctionEmitWatermark-sum");

        env.execute();
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();

        sourceFunctionEmitWatermark();
    }

    public static class CustomerProcessWindowFunction extends ProcessWindowFunction<Tuple3<String, Long, Integer>, Tuple3<String, Long, Integer>, Tuple, TimeWindow> {
        @Override
        public void process(Tuple key, Context context, Iterable<Tuple3<String, Long, Integer>> elements, Collector<Tuple3<String, Long, Integer>> out) throws Exception {
            Integer sum = 0;
            String keyValue = key.getField(0);
            for (Tuple3<String, Long, Integer> tuple2 : elements
            ) {
                sum += tuple2.f2;
            }

            if (keyValue.equalsIgnoreCase("a")||keyValue.equalsIgnoreCase("测试源_0")) {
                System.out.printf("%s 至 %s  %d-%d  watermark: %s,%d\n"
                        , WatermarksTest.dateFormat.format(context.window().getStart()),
                        WatermarksTest.dateFormat.format(context.window().getEnd()), context.window().getStart(), context.window().getEnd(),
                        WatermarksTest.dateFormat.format(context.currentWatermark()), context.currentWatermark()
                );
            }
            out.collect(Tuple3.of(keyValue, 1L, sum));
        }
    }

}
