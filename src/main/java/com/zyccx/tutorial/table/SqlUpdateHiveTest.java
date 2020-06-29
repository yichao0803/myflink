package com.zyccx.tutorial.table;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

public class SqlUpdateHiveTest {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // 初始化 table 环境
        // EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
        // TableEnvironment tableEnv = TableEnvironment.create(settings);

//
//        EnvironmentSettings fsSetting = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
//        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
//        fsEnv.setParallelism(1);
//        StreamTableEnvironment fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSetting);
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSettings);

//        String name            = "myhive";
//        String defaultDatabase = "jh_rep";
//        String hiveConfDir     = "E:\\GitOsChina\\myflink\\hive-conf";
//        String version         = "1.1.0"; // or 1.2.1
//
//        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir, version);
//        fsTableEnv.registerCatalog("myhive", hive);

        DataStream<Tuple3<String, Integer, Integer>> streamSource
                = fsEnv.fromCollection(KeyByData.getSource());

        fsTableEnv.registerDataStream("mytable", streamSource);
        // compute revenue for all customers from France and emit to "CsvSinkTable"

        fsTableEnv.sqlUpdate("INSERT INTO t1 SELECT f0, f1, f2 FROM mytable WHERE f0='偶数'");


        // execute
        fsTableEnv.execute("java_job");
    }
}
