package day03.com.demo;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

public class PostgresqlUtil {

    private static DruidDataSource ds;

    static {
        ds = new DruidDataSource();
        ds.setUrl("jdbc:postgresql://127.0.0.1:5432/postgres");
        ds.setUsername("postgres");
        ds.setPassword("root");
        ds.setDriverClassName("org.postgresql.Driver");

    }

    public static Connection getConnection() throws SQLException {
        return  ds.getConnection();
    }
}
