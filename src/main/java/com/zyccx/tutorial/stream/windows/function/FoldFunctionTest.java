package com.zyccx.tutorial.stream.windows.function;

import com.zyccx.tutorial.stream.WatermarksTest;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 测试增量聚合函数 FoldFunction
 */
public class FoldFunctionTest {

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
        SingleOutputStreamOperator<Integer> fold = operator
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .fold(0, new SumFoldFunction());

        source.print("FoldFunctionTest-source");
        fold.print("FoldFunctionTest-fold");

        env.execute("assignAscendingTimestampsTest");
    }

    public static void main(String[] args) throws Exception {
        assignAscendingTimestampsPunctuatedTest();
    }

    public static class SumFoldFunction implements  FoldFunction <Tuple3<String, Long, Integer>, Integer>{
        @Override
        public Integer fold(Integer accumulator, Tuple3<String, Long, Integer> value) throws Exception {
            return accumulator + value.f2;
        }
    }

}
