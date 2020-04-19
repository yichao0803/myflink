package com.zyccx.tutorial.process;

import org.junit.Test;

import static org.junit.Assert.*;

public class CountWithTimeoutFunctionTest {

    @Test
    public void open() {
    }

    @Test
    public void processElement() {
    }

    @Test
    public void onTimer() {
    }

    @Test
    public static void main(String[] args) {
        // 数据源
        DataStream<Tuple2<String, String>> stream = ...;

// 对KeyedStream应用ProcessFunction
        DataStream<Tuple2<String, Long>> result = stream
                .keyBy(0)
                .process(new CountWithTimeoutFunction());
    }
}