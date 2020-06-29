package com.zyccx.tutorial.table;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;

public class SqlUpdateTest {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // 初始化 table 环境
        EnvironmentSettings fsSetting = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        fsEnv.setParallelism(1);
        StreamTableEnvironment fsTableEnv = StreamTableEnvironment.create(fsEnv, fsSetting);

        DataStream<Tuple3<String, Integer, Integer>> streamSource
                = fsEnv.fromCollection(KeyByData.getSource());

        //  create a TableSink
        TableSink sink = new CsvTableSink("sql-update-outputTable");
        // register the TableSink with a specific schema
        String[] fieldNames = {"a", "b", "c"};
        TypeInformation[] fieldTypes = {Types.STRING, Types.INT, Types.INT};
        fsTableEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, sink);

        fsTableEnv.registerDataStream("mytable", streamSource);
        // compute revenue for all customers from France and emit to "CsvSinkTable"

        fsTableEnv.sqlUpdate("INSERT INTO CsvSinkTable SELECT f0, f1, f2 FROM mytable WHERE f0='偶数'");


        // execute
        fsTableEnv.execute("java_job");
    }
}
