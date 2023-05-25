package org.example;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        /* Database Connection TEST */
        String url = "jdbc:postgresql://pg.germanmerkel.com.ar/test";

        Properties props = new Properties();
        props.setProperty("user", System.getenv("DB_USERNAME"));
        props.setProperty("password", System.getenv("DB_PASSWORD"));
        props.setProperty("ssl", "false");

        Connection conn = DriverManager.getConnection(url, props);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT id, domain FROM click");
        while ( rs.next() ) {
            logger.info(rs.getString("id"));
            logger.info(rs.getString("domain"));
        }
        rs.close();
        st.close();
        conn.close();

        /* Create SERVER */
        Javalin.create(/*config*/)
            .get("/", ctx -> ctx.result("Hello World"))
            .start(8080);
    }
}