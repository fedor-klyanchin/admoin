package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Contains extends Compare {
    private static String tablePath = "action/type/string/compare/string_compare_contains";
    public static ConcurrentMap<Integer, Contains> map = new ConcurrentHashMap<>();

    @Override
    public String getControlValue() {
        return controlValue;
    }

    public Contains(String controlValue) {
        super(controlValue);
    }

    @Override
    protected void compare() {
        result = currentValue.contains(controlValue);
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionStringCompareContains.getFromYandexDataBase()");

        Log.logger.info("new ActionStringCompareContains[]");
        ConcurrentHashMap<Integer, Contains> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", Type.getTableName(tablePath)));
            String controlValue = DataBase.getColumnString(result, "?_control_value".replace("?", Type.getTableName(tablePath)));

            Contains action = new Contains(controlValue);

            actionMap.put(actionId, action);

            Log.logger.info("New actionStringCompareContains [actionId=" + actionId + ", controlValue=" + controlValue + "]");
        } while (result.next());

        Contains.map = actionMap;
    }
}