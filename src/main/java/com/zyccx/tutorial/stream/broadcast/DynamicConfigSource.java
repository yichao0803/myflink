package com.zyccx.tutorial.stream.broadcast;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.concurrent.TimeUnit;

public class DynamicConfigSource implements SourceFunction<Tuple2<String,String>> {

    private volatile boolean isRunning = true;

    @Override
    public void run(SourceContext<Tuple2<String, String>> ctx) throws Exception {
        long idx = 1;
        while (isRunning){
            ctx.collect(Tuple2.of("demoConfigKey","value" + idx));
            idx++;
            if(idx>=5) {
                isRunning=false;
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}