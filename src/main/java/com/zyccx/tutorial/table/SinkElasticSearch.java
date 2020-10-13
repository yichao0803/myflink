package com.zyccx.tutorial.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/**
 * 通过 SQL DDL 语句，输出数据到 ES
 */
public class SinkElasticSearch {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().build();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
        DataStream<Student> source = env.fromElements(
                  Student.builder().name("李雷").age(21).sex("男").build()
                , Student.builder().name("张三").age(21).sex("男").build()
                , Student.builder().name("李四").age(19).sex("男").build()
                , Student.builder().name("韩梅梅").age(18).sex("女").build()
        );
        Table table = tEnv.fromDataStream(source);
        tEnv.registerTable("t_tmp",table);
        tEnv.sqlUpdate("CREATE TABLE t_student (\n" +
                "s_name STRING,\n" +
                "s_age INT,\n" +
                "s_sex STRING \n" +
                ") WITH (\n" +
                " 'connector.type' = 'elasticsearch', \n" +
                " 'connector.version' = '6', \n" +
                " 'connector.hosts' = 'http://192.168.100.37:9200;http://192.168.100.220:9201;http://192.168.100.30:9202',\n" +
                " 'connector.index' = 'student',  \n" +
                " 'connector.document-type' = '_doc',   \n" +
                " 'update-mode' = 'append', \n" +
                " 'format.type' = 'json' " +
                ")\n");

        tEnv.sqlUpdate("INSERT INTO t_student SELECT name,age,sex FROM t_tmp ");

        env.execute("SinkElasticSearchJob");

    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Student
    {
        private String name;
        private Integer age;
        private String sex;
    }

}
