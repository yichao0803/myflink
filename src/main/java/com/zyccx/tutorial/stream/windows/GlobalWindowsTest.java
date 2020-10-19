package com.zyccx.tutorial.stream.windows;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class GlobalWindowsTest {
    /**
     * 在 SourceFunction 中生成* watermark
     */
    private static void sourceFunctionEmitWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<Tuple3<String, Long, Integer>> dssStr = env.addSource(new WatermarksTest.CustomerSource(200,false));
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = dssStr
                .keyBy(0)
                .window(GlobalWindows.create())
                .process(new CustomerProcessGlobalWindowFunction());

        sum.print("sourceFunctionEmitWatermark-sum");

        env.execute();
    }

    public static void main(String[] args) throws Exception {
        sourceFunctionEmitWatermark();
    }

    public static class CustomerProcessGlobalWindowFunction extends ProcessWindowFunction<Tuple3<String, Long, Integer>, Tuple3<String, Long, Integer>, Tuple, GlobalWindow> {
        @Override
        public void process(Tuple key, Context context, Iterable<Tuple3<String, Long, Integer>> elements, Collector<Tuple3<String, Long, Integer>> out) throws Exception {
            Integer sum = 0;
            String keyValue = key.getField(0);
            for (Tuple3<String, Long, Integer> tuple2 : elements
            ) {
                sum += tuple2.f2;
            }

            if (keyValue.equalsIgnoreCase("a")||keyValue.equalsIgnoreCase("测试源_0")) {
                System.out.printf(" maxTimestamp: %s,%d\n"
                        , WatermarksTest.dateFormat.format(context.window().maxTimestamp()),context.window().maxTimestamp()
                );
            }
            out.collect(Tuple3.of(keyValue, 1L, sum));
        }
    }
}
