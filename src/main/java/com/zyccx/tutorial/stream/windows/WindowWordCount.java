package com.zyccx.tutorial.stream.windows;

import com.zyccx.tutorial.stream.WordCount;
import com.zyccx.tutorial.stream.util.WordCountData;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 通过过 Windows 实现统计单词计数数
 * <p> 输入是纯文本文件，其行由换行符分隔。
 */
public class WindowWordCount {
    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> text;
        if (params.has("input")) {
            // 自定义输入
            text = env.readTextFile(params.get("input"));

        } else {

            text = env.fromElements(WordCountData.WORDS_ONE_LINE);

        }
        //
        env.getConfig().setGlobalJobParameters(params);

        final int windowSize = params.getInt("window", 10);
        final int slideSize = params.getInt("slide", 5);

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = text
                .flatMap(new WordCount.Tokenizer())
                .keyBy(0)
                .countWindow(windowSize, slideSize)
                .sum(1);

        if(params.has("output")){
            sum.writeAsText(params.get("output"));
        }else {
            sum.print();
        }

        env.execute("WindowWordCount");

    }
}
