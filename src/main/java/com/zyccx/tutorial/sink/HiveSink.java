package com.zyccx.tutorial.sink;

import java.sql.*;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import java.sql.PreparedStatement;

public class HiveSink extends RichSinkFunction<Tuple3<String,Integer, Integer>> {
    private PreparedStatement state ;
    private Connection conn ;
    private String querySQL = "";
//    private String sql;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        conn = getConnection();

        querySQL = "insert into t1(str,v1,v2) values(? ,?, ?)";
        state = conn.prepareStatement(querySQL);

    }

    @Override
    public void close() throws Exception {
        super.close();
        if (state != null) {
            state.close();
        }
        if (conn != null) {
            conn.close();
        }

    }

    @Override
    public void invoke(Tuple3<String,Integer, Integer> value, Context context) throws Exception {
        state.setString(1,value.f0);
        state.setInt(2,value.f1);
        state.setInt(3,value.f2);

        state.executeUpdate();
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            String jdbc = "org.apache.hive.jdbc.HiveDriver";
            String url = "jdbc:hive2://192.168.100.203:10000/jh_rep";
            String user = "hue";  // 重要！此处必须填入具有HDFS写入权限的用户名，比如hive文件夹的owner
            String password = "hue_123";
            Class.forName(jdbc);
            conn = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public static void main(String[] args) {
        getConnection();
    }

}