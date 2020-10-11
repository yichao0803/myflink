package com.zyccx.tutorial.stream.broadcast;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.concurrent.TimeUnit;

public class RandomWordSource extends RichSourceFunction<String> {

    private Boolean isRunning = true;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        Integer idx = 1;

        while (isRunning) {
            ctx.collect(idx.toString());
            idx++;
            TimeUnit.SECONDS.sleep(10);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
