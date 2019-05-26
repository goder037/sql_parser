package com.lightbend.akka.sample;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        String sql = "ALTER TABLE test01 ALTER COLUMN name TYPE  VARCHAR(50),\n" +
                "\tALTER COLUMN age TYPE  VARCHAR(50);";

        final String dbType = JdbcConstants.POSTGRESQL;
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, dbType);
    }
}
