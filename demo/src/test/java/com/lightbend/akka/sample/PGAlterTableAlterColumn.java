//package com.lightbend.akka.sample;
//
//import com.alibaba.druid.sql.ast.SQLName;
//import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
//import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
//import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
//import com.alibaba.druid.sql.dialect.postgresql.ast.PGSQLObjectImpl;
//import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
//
//public class PGAlterTableAlterColumn extends PGSQLObjectImpl implements SQLAlterTableItem {
//
//    private SQLColumnDefinition newColumnDefinition;
//
//    private boolean             first;
//
//    private SQLName             firstColumn;
//    private SQLName             afterColumn;
//
//    @Override
//    public void accept0(PGASTVisitor visitor) {
//        if (visitor.visit(this)) {
//            acceptChild(visitor, newColumnDefinition);
//
//            acceptChild(visitor, firstColumn);
//            acceptChild(visitor, afterColumn);
//        }
//    }
//}
