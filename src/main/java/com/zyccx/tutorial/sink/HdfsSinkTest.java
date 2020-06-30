package com.zyccx.tutorial.sink;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple8;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;


public class HdfsSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.100.80:9092");
        properties.setProperty("zookeeper.connect", "192.168.100.80:2181");
        properties.setProperty("group.id", "test-02");
        properties.setProperty("auto.offset.reset","earliest");// 设置从起始位置消费

        DataStreamSource<String> topic = env.addSource(
                new FlinkKafkaConsumer<>("rep-e9eb0ba39ea6401d827dd1d89da240a0-expression-data",
                        new SimpleStringSchema(), properties));

        SingleOutputStreamOperator<String> ds =
                topic
                .map(new MapFunction<String, RuleResult>() {
                    @Override
                    public RuleResult map(String value) throws Exception {
                        return JSONObject.parseObject(value,RuleResult.class);
                    }
                })
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<RuleResult>() {
                    @Override
                    public long extractAscendingTimestamp(RuleResult element) {
                        if(element.getTimeStamp()!=null){
                            return element.getTimeStamp().getTime();
                        }
                        return new Date().getTime();

                    }
                })
                .map(new MapFunction<RuleResult, String>() {
                    @Override
                    public String map(RuleResult value) throws Exception {
//                        return new Tuple8(value.getEndMark(),value.getOrgId(),value.getRuleId(),value.getRuleResult(),
                                //value.getSerialNumber(),value.getTimeStamp(),value.getUploadId(),value.getUuid());
                        return   value.getEndMark() + ","
                                + value.getOrgId() + ","
                                + value.getRuleId() + ","
                                + value.getRuleResult() + ","
                                + value.getSerialNumber() + ","
                                + value.getTimeStamp()+ ","
                                + value.getUploadId() + ","
                                + value.getUuid();
                    }
                });

        // 写入HDFS
        BucketingSink<String> bucketingSink = new BucketingSink<>("hdfs://MDSnn02:8020/flink/output/jh_rep/rep_rule_result/");
        // 设置以yyyyMMdd的格式进行切分目录，类似hive的日期分区
        bucketingSink.setBucketer(new DateTimeBucketer<>("yyyyMMdd", ZoneId.of("Asia/Shanghai")));
        // 设置文件块大小128M，超过128M会关闭当前文件，开启下一个文件
        bucketingSink.setBatchSize(1024 * 1024 * 128L);
        // 设置一小时翻滚一次
        bucketingSink.setBatchRolloverInterval(60 * 60 * 1000L);
        // 设置等待写入的文件前缀，默认是_
        bucketingSink.setPendingPrefix("");
        // 设置等待写入的文件后缀，默认是.pending
        bucketingSink.setPendingSuffix("");
        //设置正在处理的文件前缀，默认为_
        bucketingSink.setInProgressPrefix(".");
        //ds.print();
        //ds.writeAsCsv("hdfs://MDSnn02:8020/flink/output/jh_rep/rep_rule_result/", FileSystem.WriteMode.OVERWRITE);
        ds.addSink(bucketingSink);


        env.execute("RMQAndBucketFileConnectSink");
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
   public  static class RuleResult implements  Serializable {
        /**
         * 流水号
         */
        private String serialNumber;
        /**
         * 机构id
         */
        private Integer orgId;
        /**
         * 上报次
         */
        private Integer uploadId;
        /**
         * 规则id
         */
        private Integer ruleId;
        /**
         * 人群范围Id
         */
        private String peopleId;
        /**
         * uuid
         */
        private String uuid;

        /**
         * 规则结果 0未通过 1通过
         */
        private Integer ruleResult;
        /**
         * 关联键，
         */
        private String ruleRelationKey;
        /**
         * 生成时间
         */
        @JSONField(name = "timeStamp", format = "yyyy-MM-dd HH:mm:ss")
        private Date timeStamp;
        /**
         * 结束标识
         */
        private String endMark;

    }
}
