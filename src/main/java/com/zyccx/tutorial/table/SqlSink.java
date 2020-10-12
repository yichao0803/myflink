package com.zyccx.tutorial.table;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class SqlSink {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        DataStreamSource<Tuple2<String, Integer>> source = env.fromElements(Tuple2.of("张三", 21), Tuple2.of("李四", 22));
        Table table = tEnv.fromDataStream(source, "name,age");
        tEnv.registerTable("students", table);

        tEnv.toRetractStream(tEnv.scan("students"), Row.class).print("students");
        tEnv.sqlUpdate("CREATE TABLE fs_table (\n" +
                "  my_name STRING,\n" +
                "  my_age INT\n" +
                ") WITH (\n" +
                "  'connector.type' = 'filesystem', \n" +
                "  'connector.path' =' file:///E:\\GitOsChina\\myflink\\fs_table.csv',\n" +
                "  'format.type' ='csv' "+
                ")");

       tEnv.sqlUpdate("INSERT INTO fs_table SELECT name,age from students");

        env.execute("SqlSink job");
    }
}
