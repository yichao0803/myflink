package com.zyccx.tutorial.stream;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * MyKeyBySelector 多字段分组
 */
public class MyKeyBySelector implements KeySelector<Tuple3<String, Integer, Integer>, String> {
    @Override
    public String getKey(Tuple3<String, Integer, Integer> value) throws Exception {
        return "key_"+value.f0+value.f1;
    }
}