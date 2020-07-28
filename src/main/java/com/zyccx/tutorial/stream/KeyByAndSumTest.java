package com.zyccx.tutorial.stream;

import com.zyccx.tutorial.sink.HiveSink;
import com.zyccx.tutorial.stream.function.MyKeyBySelector;
import com.zyccx.tutorial.stream.util.KeyByData;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * KeyBy:从逻辑上将流划分为不相交的分区。具有相同键的所有记录都分配给同一分区。在内部，keyBy（）是通过哈希分区实现的。有多种指定密钥的方法。
 * <p>
 * KeyByAndSumTest
 * 参考资料：https://blog.csdn.net/wangpei1949/article/details/101625394
 */
public class KeyByAndSumTest {


    /**
     * main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Tuple3<String, Integer, Integer>> source = env.fromCollection(KeyByData.getSource());
        DataStreamSource<Tuple3<String, Integer, Integer>> sourceSameValue = env.fromCollection(KeyByData.getSourceSameValue());

        source.addSink(new HiveSink());

        KeyedStream<Tuple3<String, Integer, Integer>, Tuple> keyedStream = source.keyBy(0);
        KeyedStream<Tuple3<String, Integer, Integer>, String> keyedStream1 = source.keyBy(new MyKeyBySelector());

        KeyedStream<Tuple3<String, Integer, Integer>, String> keyedStreamSameValue = sourceSameValue.keyBy(new MyKeyBySelector());

        // max(field) 与 maxBy(field) 的区别: maxBy 可以返回 field 最大的那条数据;而 max 则是将最大的field的值赋值给第一条数据并返回第一条数据。
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> min = keyedStream.min(1);
        SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> minBy = keyedStream1.minBy(1);
        // minBy 时，当比较值相等，可以参数 first 控制返回第一个元素，还是最后一个元素
        //SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> minByFalse = keyedStreamSameValue.minBy(1, false);
        //SingleOutputStreamOperator<Tuple3<String, Integer, Integer>> minByTrue = keyedStreamSameValue.minBy(1, true);

        min.print("min");
        minBy.print("minBy");


//        minByFalse.print("minByFalse");
//        minByTrue.print("minByTrue");

        env.execute(" key by and sum test.");
    }
}
