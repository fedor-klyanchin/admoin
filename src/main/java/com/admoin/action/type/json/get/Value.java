package com.admoin.action.type.json.get;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tech.ydb.table.result.ResultSetReader;

public class Value implements Serializable {
    private static final long serialVersionUID = 19L;

    private static String tablePath = "action/type/json/get/value";
    private static String tableName = Type.getTableName(tablePath);

    public String name;

    public Value(
            String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ConcurrentMap<Integer, Value> getFromDataBase() {
        Log.logger.info("ActionFileValue.getFromYandexDataBase()");

        Log.logger.info("new ActionFileValue[]");
        ConcurrentHashMap<Integer, Value> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String name = DataBase.getColumnString(result, "?_name".replace("?", tableName));

            Value action = new Value(name);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetValue [actionId=" + actionId + ", name=" + name + "]");
        } while (result.next());

        return actionMap;
    }

    public static JsonObject getJsonObject(String jsonString) {
        JsonObject jsonObject = null;
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            jsonObject = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
        return jsonObject;
    }

    public String start(String source) {
        Log.logger.info("getJsonElementValueByName.start(" + name + ")");

        String result = "";

        JsonObject jsonObject = Value.getJsonObject(source);
        if (jsonObject != null) {
            result = jsonObject.get(this.name).getAsString();
        }

        return result;
    }
}