package com.lightbend.akka.sample;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import net.sf.jsqlparser.JSQLParserException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlterSqlDemo {
    public static Map<String, String> exchangeAutoIncrementTypeDict = new HashMap<>();
    public static Map<String, String> exchangeColumnTypeDict = new HashMap<>();


    static {
        exchangeAutoIncrementTypeDict.put("INT", "SERIAL");
        exchangeAutoIncrementTypeDict.put("SMALLINT", "SMALLSERIAL");
        exchangeAutoIncrementTypeDict.put("BIGINT", "BIGSERIAL");

        exchangeColumnTypeDict.put("FLOAT", "REAL");
        exchangeColumnTypeDict.put("TINYINT", "SMALLINT");
        exchangeColumnTypeDict.put("MEDIUMINT", "INTEGER");
        exchangeColumnTypeDict.put("TINYINT UNSIGNED", "SMALLINT");
        exchangeColumnTypeDict.put("SMALLINT UNSIGNED", "INTEGER");
        exchangeColumnTypeDict.put("MEDIUMINT UNSIGNED", "INTEGER");
        exchangeColumnTypeDict.put("INT UNSIGNED", "BIGINT");
        exchangeColumnTypeDict.put("BIGINT UNSIGNED", "NUMERIC(20)");
        exchangeColumnTypeDict.put("FLOAT UNSIGNED", "REAL");
        exchangeColumnTypeDict.put("DOUBLE", "DOUBLE PRECISION");
        exchangeColumnTypeDict.put("DECIMAL", "NUMERIC");
        exchangeColumnTypeDict.put("DATETIME", "TIMESTAMP");
        exchangeColumnTypeDict.put("TIME", "INTERVAL");
        exchangeColumnTypeDict.put("TINYBLOB", "BYTEA");
        exchangeColumnTypeDict.put("MEDIUMBLOB", "BYTEA");
        exchangeColumnTypeDict.put("LONGBLOB", "BYTEA");
        exchangeColumnTypeDict.put("TINYTEXT", "TEXT");
        exchangeColumnTypeDict.put("MEDIUMTEXT", "TEXT");
        exchangeColumnTypeDict.put("LONGTEXT", "TEXT");
    }

    public static void main(String[] args) throws JSQLParserException {
        String alterSql_01 = "ALTER TABLE test01 MODIFY COLUMN name VARCHAR(50);\n ";
        String alterSql_02 = "ALTER TABLE users_1 \n" +
                "\tMODIFY COLUMN name VARCHAR(50),\n" +
                "\tMODIFY COLUMN age VARCHAR(20);";
        String alterSql_03 = "ALTER TABLE users_1 \n" +
                "\tMODIFY COLUMN name VARCHAR(50),\n" +
                "\tMODIFY COLUMN age VARCHAR(20) NOT NULL COMMENT 'test',\n" +
                "\tADD COLUMN address TINYTEXT NOT NULL COMMENT '地址';\n";
        String alterSql_04 = "\tALTER TABLE users_1 ADD COLUMN ( aa1 int, aa2 INT)";
        String alterSql_05 = "ALTER TABLE users_1 ADD COLUMN ( aa1 int, aa2 INT)";
        //修改表名
        String alterSql_06 = "alter table test_a rename to sys_app; ";
        //修改表注释
        String alterSql_07 = " alter table sys_application comment '系统信息表'; ";
        String alterSql_08 = " alter table sys_application comment='系统信息表'; ";

        //修改字段注释
        String alterSql_09 = "\tALTER TABLE users_1 ALTER COLUMN name SET DEFAULT '刘德华';";

        //增加主键
        String alterSql_10 = "alter table t_app add aid int(5) not null ,add primary key (aid); ";

        //增加自增主键
        String alterSql_11 = "alter table t_app add aid int(5) not null auto_increment ,add primary key (aid); ";

        //修改为自增主键
        String alterSql_12 = "alter table t_app  modify column aid int(5) auto_increment ;";

        //修改字段名字
        String alterSql_13 = "alter table t_app change name app_name varchar(20) not null;";

        //删除字段
        String alterSql_14 = "ALTER TABLE t2 DROP COLUMN c, DROP COLUMN d; ";

        //增加删除字段
        String alterSql_15 = "ALTER TABLE users_1 ADD COLUMN tt VARCHAR(23)  ,\n" +
                "DROP COLUMN address123; ";

        final String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(alterSql_15, dbType);
        StringBuffer sqlBuffer = new StringBuffer();
        StringBuffer commentsBuffer = new StringBuffer();
        for (int i = 0; i < sqlStatements.size(); i++) {
            StringBuffer sqlStatementBuffer = new StringBuffer();
            SQLStatement sqlStatement = sqlStatements.get(i);
            if (sqlStatement instanceof MySqlRenameTableStatement) {
                MySqlRenameTableStatement renameTableStatement = (MySqlRenameTableStatement) sqlStatement;
                List<MySqlRenameTableStatement.Item> items = renameTableStatement.getItems();
                for (int j = 0; j < items.size(); j++) {
                    MySqlRenameTableStatement.Item item = items.get(j);
                    String oldTableName = item.getName().getSimpleName();
                    String newTableName = item.getTo().getSimpleName();
                    sqlStatementBuffer.append(String.format(" ALTER TABLE %s RENAME TO %s; ", oldTableName, newTableName));
                }
            } else if (sqlStatement instanceof SQLAlterTableStatement) {
                SQLAlterTableStatement alterStatement = (SQLAlterTableStatement) sqlStatement;
                List<SQLAlterTableItem> items = alterStatement.getItems();
                String tableName = alterStatement.getTableSource().getName().getSimpleName();
                sqlStatementBuffer.append(String.format(" ALTER TABLE %s ", tableName));
                for (int j = 0; j < items.size(); j++) {
                    SQLAlterTableItem sqlAlterTableItem = items.get(j);
                    if (sqlAlterTableItem instanceof MySqlAlterTableOption) {
                        MySqlAlterTableOption alterTableOption = (MySqlAlterTableOption) sqlAlterTableItem;
                        if ("COMMENT".equalsIgnoreCase(alterTableOption.getName())) {
                            commentsBuffer.append(String.format(" COMMENT ON TABLE %s IS %s; ", tableName, alterTableOption.getValue()));
                            sqlStatementBuffer.setLength(0);//清空当前sql语句的缓存，只有表注释
                        }
                    } else if (sqlAlterTableItem instanceof MySqlAlterTableModifyColumn) {
                        MySqlAlterTableModifyColumn mySqlAlterTableModifyColumn = (MySqlAlterTableModifyColumn) sqlAlterTableItem;
                        SQLColumnDefinition newColumnDefinition = mySqlAlterTableModifyColumn.getNewColumnDefinition();
                        SQLName columnName = newColumnDefinition.getName();
                        String dataTypeName = newColumnDefinition.getDataType().getName();
                        if (newColumnDefinition.isAutoIncrement()) {
                            newColumnDefinition.setAutoIncrement(false);
                            dataTypeName = exchangeAutoIncrementTypeDict.get(dataTypeName.toUpperCase());
                            newColumnDefinition.getDataType().setName(dataTypeName);
                        } else {
                            String newDataTypeName = exchangeColumnTypeDict.get(dataTypeName.toUpperCase());
                            if(newDataTypeName != null){
                                dataTypeName = newDataTypeName;
                                newColumnDefinition.getDataType().setName(dataTypeName);
                            }
                        }
                        sqlStatementBuffer.append(String.format(" ALTER COLUMN  %s TYPE %s ", columnName, dataTypeName));
                        SQLExpr comment = newColumnDefinition.getComment();
                        if (comment != null) {
                            commentsBuffer.append(String.format(" COMMENT ON COLUMN %s.%s IS %s; ", tableName, columnName, comment));
                        }
                        List<SQLColumnConstraint> constraints = newColumnDefinition.getConstraints();
                        for (int m = 0; m < constraints.size(); m++) {
                            final SQLColumnConstraint constraint = constraints.get(m);
                            if (constraint instanceof SQLNotNullConstraint) {
                                sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NOT NULL ", columnName));
                            }
                            if (constraint instanceof SQLNullConstraint) {
                                sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NULL ", columnName));
                            }
                            if (m != constraints.size() - 1) {
                                sqlStatementBuffer.append(",");
                            }
                        }
                    } else if (sqlAlterTableItem instanceof SQLAlterTableAddColumn) {
                        SQLAlterTableAddColumn sqlAlterTableAddColumn = (SQLAlterTableAddColumn) sqlAlterTableItem;
                        List<SQLColumnDefinition> columns = sqlAlterTableAddColumn.getColumns();
                        for (int k = 0; k < columns.size(); k++) {
                            SQLColumnDefinition sqlColumnDefinition = columns.get(k);
                            String dataTypeName = sqlColumnDefinition.getDataType().getName();
                            if (sqlColumnDefinition.isAutoIncrement()) {
                                sqlColumnDefinition.setAutoIncrement(false);
                                dataTypeName = exchangeAutoIncrementTypeDict.get(dataTypeName.toUpperCase());
                                sqlColumnDefinition.getDataType().setName(dataTypeName);
                            }else {
                                String newDataTypeName = exchangeColumnTypeDict.get(dataTypeName.toUpperCase());
                                if(newDataTypeName != null){
                                    dataTypeName = newDataTypeName;
                                    sqlColumnDefinition.getDataType().setName(dataTypeName);
                                }
                            }
                            String columnName = sqlColumnDefinition.getName().getSimpleName();
                            sqlStatementBuffer.append(String.format(" ADD COLUMN  %s %s ", columnName, dataTypeName));
                            if (k != columns.size() - 1) {
                                sqlStatementBuffer.append(",");
                            }
                            SQLExpr comment = sqlColumnDefinition.getComment();
                            if (comment != null) {
                                commentsBuffer.append(String.format(" COMMENT ON COLUMN %s.%s IS %s; ", tableName, columnName, comment));
                            }
                            List<SQLColumnConstraint> constraints = sqlColumnDefinition.getConstraints();
                            for (int m = 0; m < constraints.size(); m++) {
                                final SQLColumnConstraint constraint = constraints.get(m);
                                if (constraint instanceof SQLNotNullConstraint) {
                                    sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NOT NULL ", columnName));
                                }
                                if (constraint instanceof SQLNullConstraint) {
                                    sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NULL ", columnName));
                                }
                                if (m != constraints.size() - 1) {
                                    sqlStatementBuffer.append(",");
                                }
                            }
                        }
                    } else if (sqlAlterTableItem instanceof MySqlAlterTableAlterColumn) {
                        MySqlAlterTableAlterColumn alterTableAlterColumn = (MySqlAlterTableAlterColumn) sqlAlterTableItem;
                        String columnName = alterTableAlterColumn.getColumn().getSimpleName();
                        String defaultValue = alterTableAlterColumn.getDefaultExpr().toString();
                        sqlStatementBuffer.append(String.format(" ALTER COLUMN  %s set DEFAULT %s ", columnName, defaultValue));
                    } else if (sqlAlterTableItem instanceof SQLAlterTableAddConstraint) {
                        SQLAlterTableAddConstraint alterTableAddConstraint = (SQLAlterTableAddConstraint) sqlAlterTableItem;
                        SQLConstraint constraint = alterTableAddConstraint.getConstraint();
                        if (constraint instanceof MySqlPrimaryKey) {
                            MySqlPrimaryKey primaryKey = (MySqlPrimaryKey) constraint;
                            List<SQLSelectOrderByItem> columns = primaryKey.getColumns();
                            sqlStatementBuffer.append(" ADD PRIMARY KEY ( ");
                            for (int k = 0; k < columns.size(); k++) {
                                SQLSelectOrderByItem sqlSelectOrderByItem = columns.get(k);
                                sqlStatementBuffer.append(sqlSelectOrderByItem.getExpr().toString());
                                if (k != columns.size() - 1) {
                                    sqlStatementBuffer.append(",");
                                }
                            }
                            sqlStatementBuffer.append(" )  ");
                        }
                    } else if (sqlAlterTableItem instanceof MySqlAlterTableChangeColumn) {
                        MySqlAlterTableChangeColumn alterTableChangeColumn = (MySqlAlterTableChangeColumn) sqlAlterTableItem;
                        String oldColumnName = alterTableChangeColumn.getColumnName().getSimpleName();
                        SQLColumnDefinition newColumnDefinition = alterTableChangeColumn.getNewColumnDefinition();
                        String newColumnName = newColumnDefinition.getName().getSimpleName();
                        String newDataTypeName = newColumnDefinition.getDataType().getName();
                        sqlStatementBuffer.append(String.format(" RENAME COLUMN %s TO %s ; ", oldColumnName, newColumnName));
                        List<SQLColumnConstraint> constraints = newColumnDefinition.getConstraints();
                        if (newColumnDefinition.isAutoIncrement()) {
                            newColumnDefinition.setAutoIncrement(false);
                            newDataTypeName = exchangeAutoIncrementTypeDict.get(newDataTypeName.toUpperCase());
                            newColumnDefinition.getDataType().setName(newDataTypeName);
                        }else {
                            String dataTypeName = exchangeColumnTypeDict.get(newDataTypeName.toUpperCase());
                            if(dataTypeName != null){
                                newDataTypeName = dataTypeName;
                                newColumnDefinition.getDataType().setName(dataTypeName);
                            }
                        }
                        sqlStatementBuffer.append(String.format(" ALTER TABLE %s ALTER COLUMN  %s TYPE %s ", tableName, newColumnName, newDataTypeName));
                        SQLExpr comment = newColumnDefinition.getComment();
                        if (comment != null) {
                            commentsBuffer.append(String.format(" COMMENT ON COLUMN %s.%s IS %s; ", tableName, newColumnName, comment));
                        }
                        for (int m = 0; m < constraints.size(); m++) {
                            final SQLColumnConstraint constraint = constraints.get(m);
                            if (constraint instanceof SQLNotNullConstraint) {
                                sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NOT NULL ", newColumnName));
                            }
                            if (constraint instanceof SQLNullConstraint) {
                                sqlStatementBuffer.append(String.format(", ALTER COLUMN  %s SET NULL ", newColumnName));
                            }
                            if (m != constraints.size() - 1) {
                                sqlStatementBuffer.append(",");
                            }
                        }
                    }else if(sqlAlterTableItem instanceof SQLAlterTableDropColumnItem){
                        SQLAlterTableDropColumnItem alterTableDropColumnItem = (SQLAlterTableDropColumnItem) sqlAlterTableItem;
                        List<SQLName> columns = alterTableDropColumnItem.getColumns();
                        // drop语法 不支持一个drop对应多个字段删除,因此这里直接获取第一个字段
                        String columnName = columns.get(0).getSimpleName();
                        sqlStatementBuffer.append(String.format(" DROP COLUMN  %s ", columnName));
                    }

                    if (j != items.size() - 1) {
                        sqlStatementBuffer.append(",");
                    }
                }
            }
            sqlBuffer.append(sqlStatementBuffer);
        }
        System.out.println(sqlBuffer);
        System.out.println(commentsBuffer);
    }
}
