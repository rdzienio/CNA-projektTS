package pl.gienius.registerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbClient {
    static String serverIP = "jdbc:mysql://localhost:3306/projekt?useSSL=false";
    static String userDB = "root";
    static String passwdDB = "";

    static Logger logger = LoggerFactory.getLogger(dbClient.class);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("JDBC driver is not here.");
            throw new RuntimeException("Cannot load JDBC driver." + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = serverIP;
        String username = userDB;
        String password = passwdDB;
        return DriverManager.getConnection(url, username, password);
    }
}
