package com.admoin.action.type.json.set;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import tech.ydb.table.result.ResultSetReader;

public class Value implements Serializable {
    private static final long serialVersionUID = 19L;

    private static String tablePath = "action/type/json/set/json_set_value";
    private static String tableName = Type.getTableName(tablePath);

    public String key;
    public String value;

    public Value(String key, String json) {
        this.key = key;
        this.value = json;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static ConcurrentMap<Integer, Value> getFromDataBase() {
        Log.logger.info("ActionFileValue.getFromYandexDataBase()");

        Log.logger.info("new ActionFileValue[]");
        ConcurrentHashMap<Integer, Value> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String key = DataBase.getColumnString(result, "?_key".replace("?", tableName));
            String value = DataBase.getColumnString(result, "?_value".replace("?", tableName));

            Value action = new Value(key, value);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetValue [actionId=" + actionId + ", name=" + key + ", value=" + value + "]");
        } while (result.next());

        return actionMap;
    }

    public String start(String source) {
        Log.logger.info("getJsonElementValueByName.start(" + key + ")");

        Gson gson = new Gson();
        TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>() {
        };
        Map<String, String> stringMap = gson.fromJson(source, mapType);
        stringMap.put(key, "1");

        return gson.toJson(stringMap);
    }
}