package com.zyccx.tutorial.sink;

import java.sql.*;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import java.sql.PreparedStatement;

public class HiveSink extends RichSinkFunction<String> {
    private PreparedStatement state ;
    private Connection conn ;
    private String querySQL = "";
//    private String sql;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        conn = getConnection();

        querySQL = "insert into transaction(transaction_id,card_number,terminal_id,transaction_time,transaction_type,amount) values(? ,?, ?, ?, ?, ?)";
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
    public void invoke(String value, Context context) throws Exception {
        String[] arryValue=value.split(",");
        for (int i = 0; i < arryValue.length; i++) {
            state.setString(i+1,arryValue[i]);
        }
        state.executeUpdate();
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            String jdbc = "org.apache.hive.jdbc.HiveDriver";
            String url = "jdbc:hive2://192.168.229.129:10000/test";
            String user = "lz";  // 重要！此处必须填入具有HDFS写入权限的用户名，比如hive文件夹的owner
            String password = "";
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