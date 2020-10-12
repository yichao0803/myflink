package com.zyccx.tutorial.table;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;

public class UnionTest {
    public static void main(String[] args) throws Exception {
        // 1、声明环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // Blink 计划器
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env,settings);
        // 2、数据源

        DataStreamSource<Tuple2<String, Integer>> students1 = env.fromElements(
                Tuple2.of("张三", 1)
                , Tuple2.of("李四", 2)
                , Tuple2.of("王五", 3)
                , Tuple2.of("孙六", 4));
        DataStreamSource<Tuple2<String, Integer>> students2 = env.fromElements(
                Tuple2.of("张三", 1)
                , Tuple2.of("李四", 2)
                , Tuple2.of("大哥", 5)
                , Tuple2.of("二哥", 6));
        DataStreamSource<Tuple3<String, Integer, Integer>> courses = env.fromElements(
                Tuple3.of("语文", 1, 90)
                , Tuple3.of("数学", 1, 89)
                , Tuple3.of("语文", 2, 99)
                , Tuple3.of("数据", 3, 99)
                , Tuple3.of("语文", 3, 80)
                , Tuple3.of("英语", 3, 80)
                , Tuple3.of("语文", 5, 80));

        tEnv.registerDataStream("students1", students1, "name,id");
        tEnv.registerDataStream("students2", students2, "name,id");
        tEnv.registerDataStream("courses", courses, "course,id,score");
        // 3、转换
        DataStream<Tuple2<String, Integer>> union = students1.union(students2);

        Table students = tEnv.scan("students1").unionAll(tEnv.scan("students2"));
        tEnv.registerTable("students", students);
        Table sqlQuery = tEnv.sqlQuery("SELECT DISTINCT name,id FROM students");

        Table studentSources = tEnv.sqlQuery("SELECT students1.name,students1.id,courses.course,courses.score  FROM students1 INNER JOIN courses ON students1.id=courses.id");


        // 4、数据汇
//        union.print("union");

//        tEnv.toRetractStream(students, Row.class).print("tEnv-Union");
//        tEnv.toRetractStream(sqlQuery, Row.class).print("tEnv-Union-distinct");
        tEnv.toRetractStream(studentSources,Row.class).print("tEnv-students-sources ----");


        // 5、执行
        env.execute("UnionTest");
    }
}
