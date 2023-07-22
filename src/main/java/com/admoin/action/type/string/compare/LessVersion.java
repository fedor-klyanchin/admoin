package com.admoin.action.type.string.compare;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class LessVersion extends Compare {
    private static String tablePath = "action/type/string/compare/string_compare_less_version";

    @Override
    public String getControlValue() {
        return controlValue;
    }

    public LessVersion(String controlValue) {
        super(controlValue);
    }

    @Override
    protected void compare() {
        result = LessVersion.isLessVersion(currentValue, controlValue);
    }

    public static boolean isLessVersion(String check, String reference) {
        boolean result;
    
        try {
            Version.parse(check);
            Version.parse(reference);
    
            List<String> versions = Arrays.asList(
                    check,
                    reference);
    
            String lastVersion = versions.stream()
                    .map(Version::parse)
                    .sorted()
                    .reduce((first, second) -> second)
                    .get()
                    .toString();
    
            result = !check.equals(lastVersion);
        } catch (Exception e) {
            if (check.equals(reference)) {
                result = false;
            } else {
                result = true;
            }
        }
    
        Log.logger.info(
                "isLess() " + "checkVersion: " + check + " referenceVersion: " + reference + " result: " + result);
        return result;
    }

    public static ConcurrentMap<Integer, LessVersion> getFromDataBase() {
        Log.logger.info("ActionStringCompareLessVersion.getFromYandexDataBase()");

        Log.logger.info("new ActionStringCompareLessVersion[]");
        ConcurrentHashMap<Integer, LessVersion> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", Type.getTableName(tablePath)));
            String controlValue = DataBase.getColumnString(result, "?_control_value".replace("?", Type.getTableName(tablePath)));

            LessVersion action = new LessVersion(controlValue);

            actionMap.put(actionId, action);

            Log.logger.info("New actionStringCompareLessVersion [actionId=" + actionId + ", controlValue=" + controlValue + "]");
        } while (result.next());

        return actionMap;
    }
}