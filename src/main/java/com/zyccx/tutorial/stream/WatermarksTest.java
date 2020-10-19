package com.zyccx.tutorial.stream;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;

public class WatermarksTest {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) throws Exception {
        // 第一种，**在 SourceFunction 中生成* watermark
        sourceFunctionEmitWatermark();
        // 第二中，**通过  DataStream API  指定
//        assignAscendingTimestampsPeriodicTest();
//
//        assignAscendingTimestampsPunctuatedTest();
    }

    /**
     * 第一种，**在 SourceFunction 中生成* watermark
     */
    private static void sourceFunctionEmitWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<Tuple3<String, Long, Integer>> dssStr = env.addSource(new CustomerSource());
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = dssStr
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(7)))
                .apply(new CustomerWindowFunctionApply());
        sum.print();

        env.execute();
    }

    /**
     * 通过  DataStream API  指定 watermarks
     *
     * @throws Exception
     */
    private static void assignAscendingTimestampsPeriodicTest() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Tuple3<String, Long, Integer>> source = env.fromElements(Tuple3.of("a", 1L, 1), Tuple3.of("b", 1L, 1), Tuple3.of("a", 3L, 1), Tuple3.of("b", 3L, 1), Tuple3.of("c", 3L, 1));

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> operator = source.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = operator.keyBy(0).timeWindow(Time.seconds(10)).sum(1);

        source.print("periodic-source ");
        sum.print("periodic-sum");

        env.execute("assignAscendingTimestampsTest");
    }

    /**
     * 通过  DataStream API  指定 watermarks
     *
     * @throws Exception
     */
    private static void assignAscendingTimestampsPunctuatedTest() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Tuple3<String, Long, Integer>> source = env.fromElements(Tuple3.of("a", 1L, 1), Tuple3.of("b", 1L, 1), Tuple3.of("a", 3L, 1), Tuple3.of("b", 3L, 1), Tuple3.of("c", 3L, 1));

        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> operator = source.assignTimestampsAndWatermarks(new PunctuatedAssigner());
        SingleOutputStreamOperator<Tuple3<String, Long, Integer>> sum = operator.keyBy(0).timeWindow(Time.seconds(10)).sum(1);

        source.print("punctuated-source");
        sum.print("punctuated-sum");

        env.execute("assignAscendingTimestampsTest");
    }


    /**
     * 第一种，在 SourceFunction 中生成**：通过 `collectWithTimestamp(T element, long timestamp)` 方法发送记录的第二个参数 `timestamp ` 即为数据的 EventTime 对应的时间戳 ，
     * 同时需要调用emitWatermark()方法生成Watermarks，表示接下来不会再有时间戳小于等于这个数值记录。
     */
    public static class CustomerSource implements SourceFunction<Tuple3<String, Long, Integer>> {
        Boolean stop;
        Integer counts;
        Boolean printSourceInfo;

        public CustomerSource() {
            this(10);
        }

        public CustomerSource(Integer counts) {
            this(counts, false);
        }

        public CustomerSource(Integer counts, Boolean printSourceInfo) {
            this.counts = counts;
            this.printSourceInfo = printSourceInfo;
            this.stop = false;
        }

        @Override
        public void run(SourceContext<Tuple3<String, Long, Integer>> ctx) throws Exception {

            for (int i = 0; i < counts; i++) {
                if (stop) break;
                Thread.sleep(1000);
                long currentTimeMillis = System.currentTimeMillis();
                if (printSourceInfo) {
                    System.out.printf("currentTime: %d, date: %s\n", currentTimeMillis, dateFormat.format(currentTimeMillis));
                }
                // 发射记录并设置事件时间
                ctx.collectWithTimestamp(Tuple3.of(String.format("测试源_%d", i % 3), 1L, 1), currentTimeMillis);
                // 发射 Watermarks
                ctx.emitWatermark(new Watermark(currentTimeMillis));
            }
        }

        @Override
        public void cancel() {
            stop = true;
        }
    }

    /**
     * CustomerWindowFunctionApply
     */
    public static class CustomerWindowFunctionApply implements WindowFunction<Tuple3<String, Long, Integer>, Tuple3<String, Long, Integer>, Tuple, TimeWindow> {

        @Override
        public void apply(Tuple key, TimeWindow window, Iterable<Tuple3<String, Long, Integer>> input, Collector<Tuple3<String, Long, Integer>> out) throws Exception {
            Integer sum = 0;
            String keyValue = key.getField(0);
            for (Tuple3<String, Long, Integer> tuple2 : input
            ) {
                sum += tuple2.f2;
            }
            if (keyValue.equalsIgnoreCase("测试源_0")) {
                System.out.printf("%s 至 %s  %d-%d\n"
                        , dateFormat.format(window.getStart()), dateFormat.format(window.getEnd()), window.getStart(), window.getEnd()
                );
            }
            out.collect(Tuple3.of(keyValue, 1L, sum));
        }
    }

    /**
     * Periodic Watermarks是根据设定时间间隔周期性地生成Watermarks,
     * Punctuated Watermarks是根据接入数据的数量生成，例如数据流中特定数据元素的数量满足条件后触发生成Watermark
     */
    public static class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<Tuple3<String, Long, Integer>> {

        private final long maxOutOfOrderness = 3500; // 3.5 seconds

        private long currentMaxTimestamp;

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }

        @Override
        public long extractTimestamp(Tuple3<String, Long, Integer> element, long previousElementTimestamp) {
            long timestamp = element.f2;
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            return timestamp;
        }
    }

    /**
     * 除了根据时间周期生成Periodic Watermark，用户也可以根据某些特殊条件生成Punctuated Watermarks，
     * 例如判断某个数据元素的当前状态，如果接入事件中状态为0则触发生成Watermarks，如果状态不为0，则不触发生成Watermarks的逻辑
     */
    public static class PunctuatedAssigner implements AssignerWithPunctuatedWatermarks<Tuple3<String, Long, Integer>> {

        private long currentMaxTimestamp;

        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(Tuple3<String, Long, Integer> lastElement, long extractedTimestamp) {
            return lastElement.f1 > 0 ? new Watermark(extractedTimestamp) : null;
        }

        @Override
        public long extractTimestamp(Tuple3<String, Long, Integer> element, long previousElementTimestamp) {
            long timeStamp = element.f2;
            currentMaxTimestamp = Math.max(timeStamp, currentMaxTimestamp);
            return timeStamp;
        }
    }


}
