package com.zyccx.tutorial.stream.windows.function;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * IncrementalAggregateProcessWindowFunctionTest
 * 通过 ReduceFunction 返回分组内  value1.fo,value.f1及 f2的聚合值（max）
 * 通过 ProcessWindowFunction 返回 窗口结束时间
 */
public class IncrementalAggregateProcessWindowFunctionTest {

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
        SingleOutputStreamOperator<Tuple4<String, Long, Integer, Long>> sum = operator
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .reduce(new CustomerMaxReduce(),
                        new ProcessWindowFunction<Tuple3<String, Long, Integer>, Tuple4<String, Long, Integer, Long>, Tuple, TimeWindow>() {
                            @Override
                            public void process(Tuple tuple, Context context, Iterable<Tuple3<String, Long, Integer>> elements,
                                                Collector<Tuple4<String, Long, Integer, Long>> out) throws Exception {
                                elements.forEach(e -> {
                                            out.collect(Tuple4.of(e.f0, e.f1, e.f2, context.window().getEnd()));
//                                            System.out.println(String.format("process,f0:%s,f1:%d,f2:%s", e.f0, e.f1, e.f2));
                                        }
                                );

                            }
                        });

        source.print("ReduceFunctionTest-source");
        sum.print("ReduceFunctionTest-sum");

        env.execute("assignAscendingTimestampsTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }

    public static class CustomerMaxReduce implements ReduceFunction<Tuple3<String, Long, Integer>> {
        /**
         * 返回 分组内  value1.fo,value.f1及 f2的聚合值（sum）
         *
         * @param value1
         * @param value2
         * @return
         * @throws Exception
         */
        @Override
        public Tuple3<String, Long, Integer> reduce(Tuple3<String, Long, Integer> value1, Tuple3<String, Long, Integer> value2) throws Exception {
            return value1.f2 > value2.f2 ? value1 : value2;
        }
    }
}
