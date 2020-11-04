package com.zyccx.tutorial.stream.windows.function;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 测试增量聚合函数：aggregateFunction 返回值： f0、聚合 f2 的平均数
 */
public class AggregateFunctionTest {
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
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = operator
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .aggregate(new CustomerAverageAggregateFunction());

        source.print("AggregateFunctionTest-source");
        sum.print("AggregateFunctionTest-average");

        env.execute("assignAscendingTimestampsTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }

    /**
     * 返回值： f0、聚合 f2 的平均数
     */
    public static class CustomerAverageAggregateFunction implements AggregateFunction<Tuple3<String, Long, Integer>, Tuple3<String, Long, Integer>, Tuple2<String, Long>> {

        @Override
        public Tuple3<String, Long, Integer> createAccumulator() {
            return Tuple3.of("", 0L, 0);
        }

        @Override
        public Tuple3<String, Long, Integer> add(Tuple3<String, Long, Integer> value, Tuple3<String, Long, Integer> accumulator) {
            return Tuple3.of(value.f0, accumulator.f1 + value.f2, accumulator.f2 + 1);
        }

        @Override
        public Tuple2<String, Long> getResult(Tuple3<String, Long, Integer> accumulator) {
            return Tuple2.of(accumulator.f0, accumulator.f1 / accumulator.f2);
        }

        @Override
        public Tuple3<String, Long, Integer> merge(Tuple3<String, Long, Integer> a, Tuple3<String, Long, Integer> b) {
            return Tuple3.of(b.f0, a.f1 + b.f1, a.f2 + b.f2);
        }
    }


}
