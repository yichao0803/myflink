package com.zyccx.tutorial.source;

import com.zyccx.tutorial.common.ConfigKeys;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HiveTest {


    public static void main(String[] arg) throws Exception {
        String table = "inpat_operation";
        try {

            long time = System.currentTimeMillis();
            System.out.println("开始同步hive-" + table + ";" );
            /**
             * 初始化环境
             */
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            env.setParallelism(1);

            DataStreamSource<Tuple2<String, String>> source = env.addSource(new HiveReader(),"hive");

            source.print();


            env.execute("hive-" + table + " sync");

            System.out.println("同步hive-" + table + "完成，耗时:" + (System.currentTimeMillis() - time) / 1000 + "s");
        } catch (Exception e) {
            System.out.println(e);

            e.printStackTrace();
        }

    }

    private static class HiveReader extends RichSourceFunction<Tuple2<String, String>>    {
        private  final Logger logger = LoggerFactory.getLogger(JdbcReader.class);
        String driverName = "org.apache.hive.jdbc.HiveDriver";// jdbc驱动路径
        String url = "jdbc:hive2://192.168.100.203:10000/ads";// hive库地址+库名
        String user = "hue";// 用户名
        String password = "hue_123";// 密码
        String sql = "select pk_id,id from inpat_operation a limit 10000";

        private Connection connection = null;
        private PreparedStatement ps = null;

        //该方法主要用于打开数据库连接，下面的ConfigKeys类是获取配置的类
        @Override
        public void open(Configuration parameters) throws Exception {

        super.open(parameters);
        Class.forName(driverName);//加载数据库驱动
        connection = DriverManager.getConnection(url, user, password);//获取连接
        ps = connection.prepareStatement(sql);
    }

        //执行查询并获取结果
        @Override
        public void run(SourceContext<Tuple2<String, String>> ctx) throws Exception {
        try {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String pkId = resultSet.getString("pk_id");
                String id = resultSet.getString("id");
                logger.debug("HiveReader name:{}", pkId);
                Tuple2<String, String> tuple2 = new Tuple2<>();
                tuple2.setFields(id, pkId);
                ctx.collect(tuple2);//发送结果，结果是tuple2类型，2表示两个元素，可根据实际情况选择

            }
        } catch (Exception e) {
            logger.error("runException:{}", e);
        }
        finally {
            ps.close();
        }
    }

        //关闭数据库连接
        @Override
        public void cancel() {
        try {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            logger.error("runException:{}", e);
        }
    }
    }
}
