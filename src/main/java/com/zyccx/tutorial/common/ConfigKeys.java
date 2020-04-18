package com.zyccx.tutorial.common;

/**
 * TODO
 *
 * @author by Zhangyichao
 * @date 2020/1/6 15:48
 * @see ConfigKeys
 */
public class ConfigKeys {
    public static String DRIVER_CLASS() {
        return "com.mysql.jdbc.Driver";
    }

    public static String SOURCE_DRIVER_URL() {
        return "jdbc:mysql://192.168.100.86:3306/jh_rep";
    }

    public static String SOURCE_USER() {
        return "root";
    }

    public static String SOURCE_PASSWORD() {
        return "123456Ab";
    }

    public static String SOURCE_SQL() {
        return "SELECT pk_id,exp_log_id FROM REP_EXPRESSION_LOG_DATA_ORG LIMIT 100";
    }

    public static String SINK_DRIVER_URL() {
        return "jdbc:mysql://localhost:3306/soul";
    }

    public static String SINK_USER() {
        return "root";
    }

    public static String SINK_PASSWORD() {
        return "root";
    }

    public static String SINK_SQL() {
        return "root";
    }
}
