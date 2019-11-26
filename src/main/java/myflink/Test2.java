package myflink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


/**
 * Test2
 *
 * @author by Zhangyichao
 * @date 2019/11/24 22:44
 * @see Test2
 */
public class Test2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env1 =StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> text1 =env1.socketTextStream("localhost",9001,"\n");
        DataStream<Tuple3<String,String,Integer>> wordCount=text1.flatMap(new FlatMapFunction<String, Tuple3<String, String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple3<String, String, Integer>> out) throws Exception {
                try {
                    String[]  arryResult= value.split("\\s");
                    out.collect(Tuple3.of(arryResult[0],arryResult[1],Integer.parseInt(arryResult[2])));
                }catch (Exception e){

                }
            }
        });

        DataStream<Tuple3<String, String, Integer>> resultCount = wordCount
                .keyBy(0)
                .sum(2);

        resultCount.print().setParallelism(1);
        env1.execute(" Socket Window WordCount");

    }
}
