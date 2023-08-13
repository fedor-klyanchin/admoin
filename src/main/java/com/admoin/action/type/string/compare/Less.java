package com.admoin.action.type.string.compare;

import java.text.Collator;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Less extends Compare {
    private static String tablePath = "action/type/string/compare/string_compare_less";
    public static ConcurrentMap<Integer, Less> map = new ConcurrentHashMap<>();

    @Override
    public String getControlValue() {
        return controlValue;
    }

    public Less(String controlValue) {
        super(controlValue);
    }

    @Override
    protected void compare() {
        result = Less.isLess(currentValue, controlValue);
    }

    public static boolean isLess(String check, String reference) {
        boolean result;
    
        Collection<String> compareCollection = new TreeSet<>(Collator.getInstance());
        compareCollection.add(check);
        compareCollection.add(reference);
        result = !check.equals(compareCollection.toArray()[compareCollection.size() - 1]);
    
        Log.logger.info("isLess() " + " check: " + check + " reference: " + reference + " result: " + result);
        return result;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionStringCompareLess.getFromYandexDataBase()");

        String tableName = Type.getTableName(tablePath);

        Log.logger.info("new ActionStringCompareLess[]");
        ConcurrentHashMap<Integer, Less> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String controlValue = DataBase.getColumnString(result, "?_control_value".replace("?", tableName));

            Less action = new Less(controlValue);

            actionMap.put(actionId, action);

            Log.logger.info("New actionStringCompareLess [actionId=" + actionId + ", controlValue=" + controlValue + "]");
        } while (result.next());

        Less.map = actionMap;
    }
}