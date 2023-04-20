package com.aroasoft.tcpserver.mysql;


import com.aroasoft.tcpserver.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DBManager {
    public Connection conn;
    private final Logger logger = LoggerFactory.getLogger(DBManager.class);
    public static String url = ConfigProperties.getInstance().getProperty("db.metatourism.url");
    public static String dbid = ConfigProperties.getInstance().getProperty("db.metatourism.username");
    public static String dbpwd = ConfigProperties.getInstance().getProperty("db.metatourism.password");


    public void initConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url, dbid, dbpwd);
    }

        public void close() throws SQLException {

        conn.close();
    }

}
