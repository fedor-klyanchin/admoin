package com.admoin.action.type;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;

import tech.ydb.table.result.ResultSetReader;

public class Type implements Serializable {
    private static final long serialVersionUID = 7L;

    private int id;
    private String name;
    private String actionTablePath;
    private static String tablePath = "action/type/type";
    private static String tableName = Type.getTableName(tablePath);

    public void setName(String name) {
        this.name = name;
    }

    public String getActionTablePath() {
        return actionTablePath;
    }

    public void setActionTablePath(String actionTablePath) {
        this.actionTablePath = actionTablePath;
    }

    public Type(
            int id,
            String name,
            String actionTablePath) {
        this.id = id;
        this.name = name;
        this.actionTablePath = actionTablePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTablePath() {
        return tablePath;
    }

    public static String getTableName(String tablePath) {
        return tablePath.replace("action/type/", "").replace("/", "_");
    }

    public static ConcurrentMap<Integer, Type> getFromDataBase() {
        Log.logger.info("Type.getFromYandexDataBase()");

        String query = "SELECT"
                + "`?_id`,".replace("?", tableName)
                + "`?_name`,".replace("?", tableName)
                + "`?_table_path`".replace("?", tableName)
                + "FROM `" + tablePath + "`";

        ResultSetReader result = Host.dataBaseReadOnly.getQuery(query);

        Log.logger.info("new Type[]");
        ConcurrentHashMap<Integer, Type> typeMap = new ConcurrentHashMap<>();

        do {
            int id = DataBase.getColumnInt(result, "?_id".replace("?", tableName));
            String name = DataBase.getColumnString(result, "?_name".replace("?", tableName));
            String actionTablePath = DataBase.getColumnString(result, "?_table_path".replace("?", tableName));

            Type type = new Type(id, name, actionTablePath);
            typeMap.put(id, type);

            Log.logger.info("New Type. id: " + id + " name: " + name + " actionTablePath: " + actionTablePath);
        } while (result.next());

        return typeMap;
    }
}