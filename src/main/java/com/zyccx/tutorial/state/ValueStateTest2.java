package com.zyccx.tutorial.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

/**
 * 这个例子实现了一个简单的计数窗口。 我们把元组的第一个元素当作 key（在示例中都 key 都是 “1”）。
 * 该函数将出现的次数以及总和存储在 “ValueState” 中。 一旦出现次数达到 2，则将平均值发送到下游，并清除状态重新开始。
 * 请注意，我们会为每个不同的 key（元组中第一个元素）保存一个单独的值。
 */
public class ValueStateTest2 {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple2<Long, Long>> source = env.fromElements(Tuple2.of(1L, 3L), Tuple2.of(1L, 5L),
                Tuple2.of(1L, 7L), Tuple2.of(1l, 4L),
                Tuple2.of(1L, 2L));
        KeyedStream<Tuple2<Long, Long>, Tuple> keyedStream = source.keyBy(0);
        SingleOutputStreamOperator<Tuple2<Long, Long>> singleOutputStreamOperator = keyedStream.flatMap(new CountWindowAverage());
        singleOutputStreamOperator.print();

        env.execute("ValueStateTest2");


    }

    private static class CountWindowAverage extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>> {

        private transient ValueState<Tuple2<Long, Long>> sum;

        @Override
        public void open(Configuration parameters) throws Exception {
            // 状态有效期 (TTL)
            StateTtlConfig ttlConfig = StateTtlConfig
                    .newBuilder(Time.seconds(1))
                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                    .build();
            //super.open(parameters);
            ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<>(
                    "average",
                    TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
                    }),
                    Tuple2.of(0L, 0L));

            descriptor.enableTimeToLive(ttlConfig);

            sum = getRuntimeContext().getState(descriptor);
        }

        @Override
        public void flatMap(Tuple2<Long, Long> input, Collector<Tuple2<Long, Long>> out) throws Exception {
            Tuple2<Long, Long> currentSum = sum.value();
            currentSum.f0 += 1;
            currentSum.f1 += input.f1;
            sum.update(currentSum);
            if (currentSum.f0 >= 2) {
                out.collect(new Tuple2<>(input.f0, currentSum.f1 / currentSum.f0));
                sum.clear();
            }
        }
    }
}
