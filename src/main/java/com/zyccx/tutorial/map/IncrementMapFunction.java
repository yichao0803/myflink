package com.zyccx.tutorial.map;

import org.apache.flink.api.common.functions.MapFunction;

/**
 * IncrementMapFunction
 */
public class IncrementMapFunction implements MapFunction<Long, Long> {
    @Override
    public Long map(Long record) throws Exception {
        return record+1L;
    }
}
