package com.admoin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import tech.ydb.table.result.ResultSetReader;

public class Config implements Serializable {
    private static final long serialVersionUID = 1L;

    private static String query = "SELECT * FROM `config`;";
    private static Map<String, String> map = new HashMap<>();// https://docs.oracle.com/javase/8/docs/api/java/util/Hashtable.html

    public static Map<String, String> getFromDataBase() {
        String configValue;
        String configName;
        Log.logger.info("Config.getFromDataBase()");

        Log.logger.info("yandexDataBaseReadOnly.get(query)");
        ResultSetReader result = Host.dataBaseReadOnly.getQuery(query);
        if (!result.next()) {
            throw new RuntimeException("not found first_aired");
        } else {
            map.clear();
        }

        do {
            configName = result.getColumn("config_name").getText();
            configValue = result.getColumn("config_value").getText();

            Log.logger.info("map.add(" + configName + ", " + configValue + ")");
            map.put(configName, configValue);
        } while (result.next());

        return map;
    }
}
