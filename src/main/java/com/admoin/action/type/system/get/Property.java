package com.admoin.action.type.system.get;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Property implements Serializable {
    private static final long serialVersionUID = 17L;

    private static String tablePath = "action/type/system/get/system_get_property";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Property> map = new ConcurrentHashMap<>();

    public String name;

    public Property(
            String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileProperty.getFromYandexDataBase()");

        Log.logger.info("new ActionFileProperty[]");
        ConcurrentHashMap<Integer, Property> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String name = DataBase.getColumnString(result, "?_name".replace("?", tableName));

            Property action = new Property(name);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", name=" + name + "]");
        } while (result.next());
        
        Property.map = actionMap;
    }

    public String start() {
        Log.logger.info("getSystemProperty.start(" + name + ")");
        String result = "";

        result = System.getProperty(name);

        return result;
    }
}