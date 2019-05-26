package day03.com.demo;

import java.util.HashMap;
import java.util.Map;

public class CreateTableExchange {

    public static Map<String, String> exchangeAutoIncrementTypeDict = new HashMap<>();
    public static Map<String, String> exchangeColumnTypeDict = new HashMap<>();


    static {
        exchangeAutoIncrementTypeDict.put("INT", "SERIAL");
        exchangeAutoIncrementTypeDict.put("SMALLINT", "SMALLSERIAL");
        exchangeAutoIncrementTypeDict.put("BIGINT", "BIGSERIAL");

        exchangeColumnTypeDict.put("FLOAT", "REAL");
        exchangeColumnTypeDict.put("DOUBLE", "DOUBLE PRECISION");
        exchangeColumnTypeDict.put("DECIMAL", "NUMERIC");
        exchangeColumnTypeDict.put("DATETIME", "TIMESTAMP");
        exchangeColumnTypeDict.put("TIME", "INTERVAL");
        exchangeColumnTypeDict.put("LONGBLOB", "BYTEA");
        exchangeColumnTypeDict.put("LONGTEXT", "TEXT");
    }


    public static String convertCreateTableSql(String sql){
        return "";
    }


}
