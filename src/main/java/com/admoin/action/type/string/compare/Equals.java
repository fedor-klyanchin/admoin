package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Equals extends Compare {
    private static String tablePath = "action/type/string/compare/string_compare_equals";

    @Override
    public String getControlValue() {
        return controlValue;
    }

    public Equals(String controlValue) {
        super(controlValue);
    }

    @Override
    protected void compare() {
        result = currentValue.equals(controlValue);
    }

    public static ConcurrentMap<Integer, Equals> getFromDataBase() {
        Log.logger.info("ActionStringCompareEquals.getFromYandexDataBase()");

        Log.logger.info("new ActionStringCompareEquals[]");
        ConcurrentHashMap<Integer, Equals> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", Type.getTableName(tablePath)));
            String controlValue = DataBase.getColumnString(result, "?_control_value".replace("?", Type.getTableName(tablePath)));

            Equals action = new Equals(controlValue);

            actionMap.put(actionId, action);

            Log.logger.info("New actionStringCompareEquals [actionId=" + actionId + ", controlValue=" + controlValue + "]");
        } while (result.next());

        return actionMap;
    }
}