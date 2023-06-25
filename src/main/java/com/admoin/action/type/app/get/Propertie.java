package com.admoin.action.type.app.get;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Propertie implements Serializable {
    private static final long serialVersionUID = 16L;
    private static String tablePath = "action/type/app/get/propertie";
    private static String tableName = Type.getTableName(tablePath);

    private String propertieName;

    public Propertie(
            String propertieName) {
        this.propertieName = propertieName;
    }

    public String getPropertieName() {
        return propertieName;
    }

    public static ConcurrentMap<Integer, Propertie> getFromDataBase() {
        Log.logger.info("ActionAppGetPropertie.getFromYandexDataBase()");

        Log.logger.info("new ActionAppGetPropertie[]");
        ConcurrentHashMap<Integer, Propertie> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String propertieName = DataBase.getColumnString(result, "?_name".replace("?", tableName));

            Propertie action = new Propertie(propertieName);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", propertieName=" + propertieName + "]");
        } while (result.next());

        return actionMap;
    }

    public String start() {
        Log.logger.info("GetPropertie.start(" + propertieName + ")");
        String result = "";

        result = Host.properties.getProperty(propertieName, "");

        return result;
    }
}