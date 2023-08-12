package com.admoin.action.type.app.get;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Field implements Serializable {
    private static final long serialVersionUID = 15L;

    private static String tablePath = "action/type/app/get/app_get_field";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Field> map = new ConcurrentHashMap<>();

    public String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public Field(
            String fieldName) {
        this.fieldName = fieldName;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionAppGetField.getFromYandexDataBase()");

        Log.logger.info("new ActionAppGetField[]");
        ConcurrentHashMap<Integer, Field> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String fieldName = DataBase.getColumnString(result, "?_name".replace("?", tableName));

            Field action = new Field(fieldName);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", fieldName=" + fieldName + "]");
        } while (result.next());

        Field.map = actionMap;
    }

    public String start(Host host) {
        Log.logger.info("GetPropertie.start(" + fieldName + ")");
        String result = "";
        result = host.getFieldValue(fieldName);

        try {
            java.lang.reflect.Field field = Host.class.getField(fieldName);
            result = field.get(this).toString();
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Log.logger.warning(e.getMessage());
        }   

        Log.logger.info("GetPropertie.Start() return: " + result);
        return result;
    }
}