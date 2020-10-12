package com.zyccx.tutorial.stream.util;

import org.apache.flink.api.java.tuple.Tuple3;

import java.util.ArrayList;
import java.util.List;

public class KeyByData {

    /**
     * getSource
     *
     * @return
     */
    public static List<Tuple3<String, Integer, Integer>> getSource() {
        return getSource(5);
    }

    /**
     * getSource
     *
     * @return
     */
    public static List<Tuple3<String, Integer, Integer>> getSource(Integer count) {
        List<Tuple3<String, Integer, Integer>> list = new ArrayList<>();
        for (int i = count; i > 0; i--) {
            list.add(new Tuple3<>(i % 2 == 0 ? "偶数" : "奇数", i, i));
        }
        return list;
    }

    /**
     * getSource
     *
     * @return
     */
    public static List<Tuple3<String, Integer, Integer>> getSourceSameValue() {
        List<Tuple3<String, Integer, Integer>> list = new ArrayList<>();
        for (int i = 5; i > 0; i--) {
            list.add(new Tuple3<>(i % 2 == 0 ? "偶数" : "奇数", i % 2, -i));
        }
        return list;
    }
}
