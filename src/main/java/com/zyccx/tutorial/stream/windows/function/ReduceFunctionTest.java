package com.zyccx.tutorial.stream.windows.function;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 测试增量聚合函数 ReduceFunction
 */
public class ReduceFunctionTest {

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
                .reduce(new CustomerReduce());

        source.print("ReduceFunctionTest-source");
        sum.print("ReduceFunctionTest-sum");

        env.execute("assignAscendingTimestampsTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }

    public static class CustomerReduce implements ReduceFunction<Tuple3<String, Long, Integer>> {
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
            return Tuple3.of(value1.f0, value1.f1, value1.f2 + value2.f2);
        }
    }
}
