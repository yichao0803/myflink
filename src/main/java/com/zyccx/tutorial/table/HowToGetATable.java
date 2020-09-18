package com.zyccx.tutorial.table;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.OldCsv;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.sources.CsvTableSource;
import org.apache.flink.types.Row;

/**
 * 如何获取一个 Table
 * 1.通过 Table descriptor 来注册
 * 2.通过自定义 source 来注册
 * 3.通过 DataStream 来注册
 */
public class HowToGetATable {
    /**
     * main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String path = HowToGetATable.class.getClassLoader().getResource("words.txt").getPath();
        runBatchFlinkTable(path);
        runStreamFlinkTable(path);

        runBatchFlinkTable(path);
        runStreamFlinkTable(path);


    }

    /**
     * runBatchTable
     * 通过 Table descriptor 来注册
     * 通过自定义 source 来注册
     *
     * @param path 文件路径
     * @throws Exception Exception
     */
    private static void runBatchFlinkTable(String path) throws Exception {
        ExecutionEnvironment env
                = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);
        // String path2 = JavaBatchWordCount.class.getClassLoader().getResource("words.txt").getPath();
        // 1. Table descriptor
        tEnv.connect(new FileSystem().path(path))
                .withFormat(new OldCsv().field("word", Types.STRING).lineDelimiter("\n"))
                .withSchema(new Schema().field("word", Types.STRING))
                .registerTableSource("fileSource");

        Table result = tEnv.scan("fileSource")
                .groupBy("word")
                .select("word,count(1) as count");
        tEnv.toDataSet(result, Row.class).print();

        // 2. User defined table source
        CsvTableSource csvTableSource = new CsvTableSource(path,
                new String[]{"word"},
                new TypeInformation[]{Types.STRING});
        tEnv.registerTableSource("sourceTable2", csvTableSource);

        Table result2 = tEnv.scan("sourceTable2")
                .groupBy("word")
                .select("word,count(1) as count");
        tEnv.toDataSet(result2, Row.class).print();
    }

    /**
     * 通过 DataStream 来注册
     *
     * @param path 文件路径
     * @throws Exception Exception
     */
    private static void runStreamFlinkTable(String path) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        DataStreamSource<String> source = env.readTextFile(path);
        // register the DataStream as table " myTable3" with
        // fields "word"
        tEnv.registerDataStream("sourceTable3", source, "word");

        Table result = tEnv.scan("sourceTable3")
                .groupBy("word")
                .select("word,count(1) as count");

        tEnv.toRetractStream(result, Row.class).print("runStreamTable");

        tEnv.execute("stream table job");
    }


    /**
     * runBatchTable
     * 通过 Table descriptor 来注册
     * 通过自定义 source 来注册
     *
     * @param path 文件路径
     * @throws Exception Exception
     */
    private static void runBatchBlinkTable(String path) throws Exception {
//        EnvironmentSettings bbSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
//        TableEnvironment bbTableEnv=TableEnvironment.create(bbSettings);
//
//
//
////
////        ExecutionEnvironment env
////                = ExecutionEnvironment.getExecutionEnvironment();
////        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);
//        // String path2 = JavaBatchWordCount.class.getClassLoader().getResource("words.txt").getPath();
//        // 1. Table descriptor
//        bbTableEnv.connect(new FileSystem().path(path))
//                .withFormat(new OldCsv().field("word", Types.STRING).lineDelimiter("\n"))
//                .withSchema(new Schema().field("word", Types.STRING))
//                .registerTableSource("fileSource");
//
//        Table result = bbTableEnv.scan("fileSource")
//                .groupBy("word")
//                .select("word,count(1) as count");
//        bbTableEnv.toDataSet(result, Row.class).print();
//
//        // 2. User defined table source
//        CsvTableSource csvTableSource = new CsvTableSource(path,
//                new String[]{"word"},
//                new TypeInformation[]{Types.STRING});
//        bbTableEnv.registerTableSource("sourceTable2", csvTableSource);
//
//        Table result2 = bbTableEnv.scan("sourceTable2")
//                .groupBy("word")
//                .select("word,count(1) as count");
//        bbTableEnv.toDataSet(result2, Row.class).print();
    }
}
