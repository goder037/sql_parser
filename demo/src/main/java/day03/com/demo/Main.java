package day03.com.demo;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAlterColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.NullEventDataDeserializer;
import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        BinaryLogClient client = new BinaryLogClient("127.0.0.1", 3306, "root", "root");

        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG
        );
//        eventDeserializer.setEventDataDeserializer(EventType.EXT_WRITE_ROWS, new EventDataDeserializer() {
//            @Override
//            public EventData deserialize(ByteArrayInputStream inputStream) throws IOException {
//                WriteRowsEventData eventData = new WriteRowsEventData();
//                String str = inputStream.readString(inputStream.available());
//                System.out.println(str);
//                return eventData.setData();
//            }
//        });

        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(event -> {

            EventHeader eventHeader = event.getHeader();
            long timestamp = eventHeader.getTimestamp();
            Timestamp date = new Timestamp(timestamp);
            System.out.println(date);
            EventType eventType = eventHeader.getEventType();
            //根据table_map事件获取数据库和表信息
            if(EventType.TABLE_MAP.equals(eventType)){
                TableMapEventData data = event.getData();
                String database = data.getDatabase();
                String table = data.getTable();

            }
            if(EventType.EXT_WRITE_ROWS.equals(eventType)){
                WriteRowsEventData eventData = event.getData();
                List<Serializable[]> rows = eventData.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    Serializable[] serializableObject = rows.get(i);
                    System.out.println(serializableObject);
                }
            }
            System.out.println(event);

        });
        client.connect();
    }
}
