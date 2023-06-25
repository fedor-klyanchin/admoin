package com.admoin.action.type.system.get;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Variable implements Serializable {
    private static final long serialVersionUID = 14L;

    private static String tablePath = "action/type/system/get/variable";
    private static String tableName = Type.getTableName(tablePath);

    public String name;

    public Variable(
            String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ConcurrentMap<Integer, Variable> getFromDataBase() {
        Log.logger.info("ActionFileVariable.getFromYandexDataBase()");

        Log.logger.info("new ActionFileVariable[]");
        ConcurrentHashMap<Integer, Variable> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String name = DataBase.getColumnString(result, "?_name".replace("?", tableName));

            Variable action = new Variable(name);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", name=" + name + "]");
        } while (result.next());

        return actionMap;
    }

    public String start() {
        Log.logger.info("getSystemEnv.start(" + name + ")");
        String result = "";

        result = System.getenv(name);

        return result;
    }
}