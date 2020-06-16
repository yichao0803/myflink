package com.zyccx.tutorial.stream;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;


/**
 * 实现元素的重复广播，设置source的并行度为1
 */
public class StreamingDemoWithMyNoPralalleSourceBroadcast {
    public static void main(String[] args) throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(8);

        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(1);//注意：针对此source，并行度只能设置为1

        DataStream<Long> num = text.broadcast().map(new MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                long id = Thread.currentThread().getId();
                System.out.println("线程id："+id+",接收到数据：" + value);
                return value;
            }
        });


        //每2秒钟处理一次数据
        DataStream<Long> sum = num.timeWindowAll(Time.seconds(2)).sum(0);

        //打印结果
        sum.print().setParallelism(1);

        String jobName = StreamingDemoWithMyNoPralalleSourceBroadcast.class.getSimpleName();
        env.execute(jobName);
    }
    public static class MyNoParalleSource implements SourceFunction<Long> {

        private long count = 1L;

        private boolean isRunning = true;

        /**
         * 主要的方法
         * 启动一个source
         * 大部分情况下，都需要在这个run方法中实现一个循环，这样就可以循环产生数据了
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void run(SourceContext<Long> ctx) throws Exception {
            while(isRunning){
                ctx.collect(count);
                count++;
                //每秒产生一条数据
                Thread.sleep(1000);
            }
        }

        /**
         * 取消一个cancel的时候会调用的方法
         *
         */
        @Override
        public void cancel() {
            isRunning = false;
        }
    }


}
