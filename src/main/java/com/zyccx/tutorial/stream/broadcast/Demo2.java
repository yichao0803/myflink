package com.zyccx.tutorial.stream.broadcast;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.tuple.Tuple2;


/**
 * 聊聊flink的Broadcast State
 * https://cloud.tencent.com/developer/article/1378332
 */
public class Demo2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> originStream = env.addSource(new RandomWordSource());

        MapStateDescriptor<String, String> descriptor = new
                MapStateDescriptor("dynamicConfig", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        BroadcastStream<Tuple2<String, String>> configStream = env.addSource(new DynamicConfigSource()).broadcast(descriptor);

        BroadcastConnectedStream<String, Tuple2<String, String>> connectStream = originStream.connect(configStream);
        connectStream.process(new BroadcastProcessFunction<String, Tuple2<String, String>, Void>() {
            @Override
            public void processElement(String value, ReadOnlyContext ctx, Collector<Void> out) throws Exception {
                ReadOnlyBroadcastState<String, String> config = ctx.getBroadcastState(descriptor);
                String configValue = config.get("demoConfigKey");
                //do some process base on the config
                System.out.println(String.format("process value:%s,config:%s", value, configValue));
                // LOGGER.info("process value:{},config:{}",value,configValue);
            }


            @Override
            public void processBroadcastElement(Tuple2<String, String> value, Context ctx, Collector<Void> out) throws Exception {
                //LOGGER.info("receive config item:{}", value);
                System.out.println(String.format("receive config item:%s", value));
                //update state
                ctx.getBroadcastState(descriptor).put(value.getField(0), value.getField(1));
            }
        });

        env.execute("testBroadcastState");
    }
}
