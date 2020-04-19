package com.zyccx.tutorial.map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class IncrementFlatMapFunction implements FlatMapFunction<Long,Long> {
    @Override
    public void flatMap(Long value, Collector<Long> out) throws Exception {
        out.collect(value+1);
    }
}
