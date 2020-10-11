package com.zyccx.tutorial.table;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.BatchTableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.OldCsv;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.types.Row;

/**
 * 如何输出一个 Table
 * 输出 table 到 log\wordsSink.txt
 */
public class HowToEmitATable {
    public static void main(String[] args) throws Exception {
        String path = JavaBatchWordCount.class.getClassLoader().getResource("words.txt").getPath();
        batchEmitTable(path);
        streamEmitTable(path);
    }

    private static void batchEmitTable(String path) throws Exception {
        ExecutionEnvironment env
                = ExecutionEnvironment.getExecutionEnvironment();
        BatchTableEnvironment tEnv = BatchTableEnvironment.create(env);
        env.setParallelism(1);
        // 1. Table descriptor
        tEnv.connect(new FileSystem().path(path))
                .withFormat(new OldCsv().field("word", Types.STRING).lineDelimiter("\n"))
                .withSchema(new Schema().field("word", Types.STRING))
                .registerTableSource("fileSource");
        Table result = tEnv.scan("fileSource")
                .groupBy("word")
                .select("word,count(1) as count");
        tEnv.toDataSet(result, Row.class).print();

        TableSink csvSink = new CsvTableSink("log\\batch-wordsSink.txt");
        // define the field names and types
        String[] fieldNames = {"word", "counts"};
        TypeInformation[] fieldTypes = {Types.STRING, Types.LONG};
        // register the TableSink as table "CsvSinkTable"
        tEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, csvSink);

        result.insertInto("CsvSinkTable");
        tEnv.execute("CsvSinkTable-job");
    }

    private static void streamEmitTable(String path) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        DataStreamSource<String> source = env.readTextFile(path);
        // register the DataStream as table " myTable3" with
        // fields "word"
        tEnv.registerDataStream("sourceTable3", source, "word");

        Table result = tEnv.scan("sourceTable3")
                .groupBy("word")
                .select("word,count(1) as count");

        DataStream<Tuple2<Boolean, Row>> dataStream = tEnv.toRetractStream(result, Row.class);
        dataStream.print("stream table job");

        tEnv.execute("stream table job");
    }
}
