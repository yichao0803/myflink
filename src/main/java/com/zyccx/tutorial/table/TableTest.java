package com.zyccx.tutorial.table;

import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;


/**
 * table api sample flink.1.10.1
 */
public class TableTest {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

//        // 初始化 table 环境
//        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
//        bsEnv.setParallelism(1);
//        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
//        StreamTableEnvironment bsTableEnv = StreamTableEnvironment.create(bsEnv, bsSettings);
//
//        DataStream<Tuple3<String, Integer, Integer>> streamSource
//                = bsEnv.fromCollection(KeyByData.getSource());
//
//        bsTableEnv.createTemporaryView("myTable", streamSource);
//
//        Table myTable = bsTableEnv.from("myTable");
//
//        Table tapiResult =myTable
//                .filter("f0==='偶数'")
//                //.groupBy("f0, f1")
//                .select("f0, f1, f2");
//
//        //  create a TableSink
//        final Schema schema = new Schema()
//                .field("a", DataTypes.STRING())
//                .field("b", DataTypes.INT())
//                .field("c", DataTypes.INT());
//
//        bsTableEnv.connect(new FileSystem().path("update-outputTable"))
//                .withFormat(new Csv())
//                .withSchema(schema)
//                .createTemporaryTable("CsvSinkTable");
//
//        // emit the result Table to the registered TableSink
//        tapiResult.insertInto("CsvSinkTable");
//
//        // execute
//        bsTableEnv.execute("java_job");
    }
}
