package com.zyccx.tutorial.stream.broadcast;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Demo3 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<ModelKeyValue> originStream = env.addSource(new RandomWordSource3());

        MapStateDescriptor<String, String> descriptor = new
                MapStateDescriptor("dynamicConfig", BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        BroadcastStream<Tuple2<String, String>> configStream = env.addSource(new DynamicConfigSource()).broadcast(descriptor);

        BroadcastConnectedStream<ModelKeyValue, Tuple2<String, String>> connectStream = originStream.connect(configStream);
        connectStream.process(new BroadcastProcessFunction<ModelKeyValue, Tuple2<String, String>, Void>() {

            private List<ModelKeyValue> modelKeyValueArrayList = new ArrayList<>();

            @Override
            public void processElement(ModelKeyValue value, ReadOnlyContext ctx, Collector<Void> out) throws Exception {
                ReadOnlyBroadcastState<String, String> config = ctx.getBroadcastState(descriptor);

                if (config != null && config.get(value.getKey()) != null) {
                    String configValue = config.get(value.getKey());
                    //do some process base on the config
                    System.out.println(String.format("process  value:%s,config:%s", value.getValue(), configValue));
                    // LOGGER.info("process value:{},config:{}",value,configValue);
                } else {
                    modelKeyValueArrayList.add(value);
                }
            }


            @Override
            public void processBroadcastElement(Tuple2<String, String> value, Context ctx, Collector<Void> out) throws Exception {
                //LOGGER.info("receive config item:{}", value);
                System.out.println(String.format("receive config item:%s", value));
                //update state
                ctx.getBroadcastState(descriptor).put(value.getField(0), value.getField(1));


                if (modelKeyValueArrayList != null) {
                    for (int i = 0; i <modelKeyValueArrayList.size() ; i++) {
                        ModelKeyValue modelKeyValue=modelKeyValueArrayList.get(i);

                        if (modelKeyValue.getKey().equalsIgnoreCase(value.f0)) {
                            System.out.println(String.format("--- process  value:%s,config:%s", value, value.f1));
                            modelKeyValueArrayList.remove(modelKeyValue);
                        }
                    }

                }
            }
        });

        env.execute("testBroadcastState");
    }

    public static class ModelKeyValue {
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String key;
        private String value;

    }


    public static class RandomWordSource3 extends RichSourceFunction<ModelKeyValue> {

        private Boolean isRunning = true;

        @Override
        public void run(SourceContext<ModelKeyValue> ctx) throws Exception {
            Integer idx = 0;

            while (isRunning) {
                ModelKeyValue modelKeyValue = new ModelKeyValue();
                modelKeyValue.setKey("demoConfigKey" + (idx % 5));
                modelKeyValue.setValue("value" + idx.toString());

                ctx.collect(modelKeyValue);
                idx++;
                TimeUnit.SECONDS.sleep(1);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    public static class DynamicConfigSource implements SourceFunction<Tuple2<String, String>> {

        private volatile boolean isRunning = true;

        @Override
        public void run(SourceContext<Tuple2<String, String>> ctx) throws Exception {
            long idx = 0;
            while (isRunning) {
                ctx.collect(Tuple2.of("demoConfigKey" + idx, "configValue" + idx));
                idx++;
                if (idx > 5) {
                    isRunning = false;
                }

                TimeUnit.SECONDS.sleep(3);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

}

