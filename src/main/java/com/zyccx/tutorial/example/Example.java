package com.zyccx.tutorial.example;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Example {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
//        DataStream<Person> flintstones = env.fromElements(
//                new Person("张三", 19),
//                new Person("李四", 20),
//                new Person("王五", 16)
//        );

        List<Person> people = new ArrayList<Person>();
        people.add(new Person("张三", 19));
        people.add(new Person("李四", 20));
        people.add(new Person("王五", 16));
        DataStream<Person> flintstones = env.fromCollection(people);


        DataStream<Person> adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age > 18;
            }
        });
        adults.print();

        DataStream<String> jsons=adults.map(new MapFunction<Person, String>() {
            @Override
            public String map(Person person) throws Exception {
                return JSON.toJSONString(person);
            }
        });
        jsons.print();
        jsons.writeToSocket("192.168.100.32", 9999,new SimpleStringSchema());

        //adults.writeAsText("c:\\flink\\out");
//        adults.writeToSocket("192.168.100.32", 9999, new SerializationSchema<Person>() {
//            @Override
//            public byte[] serialize(Person element) {
//                ObjectOutputStream oos = null;
//                ByteArrayOutputStream bos = null;
//                try {
//                    bos = new ByteArrayOutputStream();
//                    oos = new ObjectOutputStream(bos);
//                    oos.writeObject(element);
//                    byte[] b = bos.toByteArray();
//                    return b;
//                } catch (IOException e) {
//                    System.out.println("序列化失败 Exception:" + e.toString());
//                    return null;
//                } finally {
//                    try {
//                        if (oos != null) {
//                            oos.close();
//                        }
//                        if (bos != null) {
//                            bos.close();
//                        }
//                    } catch (IOException ex) {
//                        System.out.println("io could not close:" + ex.toString());
//                    }
//                }
//            }
//        });
        //adults.writeAsCsv("c:\\flink\\out");
        env.execute();
    }

    public static class Person implements Serializable {
        public String name;
        public Integer age;

        public Person() {
        }

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return this.name + ": age " + this.age.toString();
        }
    }
}
