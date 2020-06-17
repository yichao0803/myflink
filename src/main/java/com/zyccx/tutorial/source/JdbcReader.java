package com.zyccx.tutorial.source;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.zyccx.tutorial.common.ConfigKeys;
/**
 * TODO
 *
 * @author by Zhangyichao
 * @date 2020/1/6 15:48
 * @see JdbcReader
 */
public class JdbcReader extends RichSourceFunction<Tuple2<String, String>> {
    private static final Logger logger = LoggerFactory.getLogger(JdbcReader.class);

    private Connection connection = null;
    private PreparedStatement ps = null;

    //该方法主要用于打开数据库连接，下面的ConfigKeys类是获取配置的类
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName(ConfigKeys.DRIVER_CLASS());//加载数据库驱动
        connection = DriverManager.getConnection(ConfigKeys.SOURCE_DRIVER_URL(), ConfigKeys.SOURCE_USER(), ConfigKeys.SOURCE_PASSWORD());//获取连接
        ps = connection.prepareStatement(ConfigKeys.SOURCE_SQL());
    }

    //执行查询并获取结果
    @Override
    public void run(SourceContext<Tuple2<String, String>> ctx) throws Exception {
        try {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String pkId = resultSet.getString("pk_id");
                String expLogId = resultSet.getString("exp_log_id");
                logger.debug("readJDBC name:{}", pkId);
                Tuple2<String, String> tuple2 = new Tuple2<>();
                tuple2.setFields(expLogId, pkId);
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