package main.java.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
    public static Connection getConnection() throws Exception {
        Properties props = new Properties();
        System.out.println("File path: " + DBUtil.class.getClassLoader().getResource("db-config.properties"));
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db-config.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find db-config.properties in classpath.");
            }
            props.load(input);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }
}