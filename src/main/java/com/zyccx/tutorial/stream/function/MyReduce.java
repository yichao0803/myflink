package com.zyccx.tutorial.stream.function;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * 实现 聚合 sum(f1) ,返回第一个元素
 */
public class MyReduce implements ReduceFunction<org.apache.flink.api.java.tuple.Tuple3<String, Integer, Integer>> {
    @Override
    public Tuple3<String, Integer, Integer> reduce(Tuple3<String, Integer, Integer> t1,
                                                   Tuple3<String, Integer, Integer> t2) throws Exception {
        return new Tuple3(t1.f0, t1.f1 + t2.f1, t1.f2);
    }
}
