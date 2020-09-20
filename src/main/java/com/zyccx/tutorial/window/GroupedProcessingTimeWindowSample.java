package com.zyccx.tutorial.window;

import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.HashMap;
import java.util.Random;

/**
 * 示例4. 实时订单统计示例
 * 参考地址：https://ververica.cn/developers/apache-flink-basic-zero-iii-datastream-api-programming/
 * *
 * @author by Zhangyichao
 * @date 2019/12/27 14:38
 * @see GroupedProcessingTimeWindowSample
 */
public class GroupedProcessingTimeWindowSample {

    private static class DataSource extends RichParallelSourceFunction<Tuple2<String, Integer>> {
        private volatile boolean isRunning = true;

        @Override
        public void run(SourceContext<Tuple2<String, Integer>> ctx) throws Exception {
            Random random = new Random();
            while (isRunning) {
                Thread.sleep((getRuntimeContext().getIndexOfThisSubtask() + 1) * 1000 * 5);
                String key = "类别" + (char) ('A' + random.nextInt(3));

                int value = random.nextInt(10) + 1;

                System.out.println(String.format("Emits\t(%s, %d)", key, value));
                ctx.collect(new Tuple2<>(key, value));
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env= StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<Tuple2<String,Integer>> ds =env.addSource(new DataSource());
        KeyedStream<Tuple2<String,Integer>,Tuple> keyedStream=ds.keyBy(0);

        keyedStream.sum(1).keyBy(new KeySelector<Tuple2<String,Integer>, Object>() {

            @Override
            public Object getKey(Tuple2<String, Integer> value) throws Exception {
                return "";
            }
        }).fold(new HashMap<String, Integer>(), new FoldFunction<Tuple2<String, Integer>, HashMap<String, Integer>>() {
            @Override
            public HashMap<String, Integer> fold(HashMap<String, Integer> accumulator, Tuple2<String, Integer> value) throws Exception {

                accumulator.put(value.f0,value.f1);
                return  accumulator;
            }
        }).addSink(new SinkFunction<HashMap<String, Integer>>() {
            @Override
            public void invoke(HashMap<String, Integer> value) throws Exception {
                // 每个类型的商品成交量
                System.out.println(value);
                // 商品成交总量
                System.out.println(value.values().stream().mapToInt(v->v).sum());
            }
        });

        env.execute("TestJob");
    }


}
