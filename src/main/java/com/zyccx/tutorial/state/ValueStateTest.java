package com.zyccx.tutorial.state;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 分别统计的男性和女性的平均年龄,每人输出一次结果
 */
public class ValueStateTest {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Person> source = env.fromCollection(getPerson());
        KeyedStream<Person, String> keyedStream = source.keyBy(person -> person.sex);
        SingleOutputStreamOperator<Tuple2<String, Long>> singleOutputStreamOperator = keyedStream.flatMap(new AverageFlatMap());
        singleOutputStreamOperator.print("test1");

        env.execute("value state test");

    }


    public static List<Person> getPerson() {
        List<Person> list = new ArrayList<>();
        list.add(new Person("张三", "男", 18));
        list.add(new Person("李四", "男", 20));
        list.add(new Person("王五", "男", 21));
        list.add(new Person("韩梅梅", "女", 21));
        list.add(new Person("露西", "女", 20));
        return list;
    }

    public static class Person implements Serializable {
        public String name;
        public String sex;
        public Integer age;

        public Person() {
        }

        public Person(String name, String sex, Integer age) {
            this.name = name;
            this.sex = sex;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", sex=" + sex +
                    ", age=" + age +
                    '}';
        }
    }

    private static class AverageFlatMap extends RichFlatMapFunction<Person, Tuple2<String, Long>> {

        private ValueState<Tuple3<String, Long, Long>> sum;

        @Override
        public void open(Configuration parameters) throws Exception {
            //super.open(parameters);
            ValueStateDescriptor<Tuple3<String, Long, Long>> descriptor = new ValueStateDescriptor<>(
                    "average",
                    TypeInformation.of(new TypeHint<Tuple3<String, Long, Long>>() {
                    }),
                    Tuple3.of("key", 0L, 0L)
            );
            sum = getRuntimeContext().getState(descriptor);

        }

        @Override
        public void flatMap(Person person, Collector<Tuple2<String, Long>> out) throws Exception {

            Tuple3<String, Long, Long> currentSum = sum.value();
            if ("key".equals(currentSum.f0)) currentSum.f0 = person.sex;

            currentSum.f1 += 1;
            currentSum.f2 += person.age;

            sum.update(currentSum);

            out.collect(new Tuple2<>(currentSum.f0, currentSum.f2 / currentSum.f1));
        }
    }
}
