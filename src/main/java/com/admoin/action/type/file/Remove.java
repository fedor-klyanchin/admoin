package com.admoin.action.type.file;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Remove implements Serializable {
    private static final long serialVersionUID = 12L;

    private static String tablePath = "action/type/file/file_remove";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Remove> map = new ConcurrentHashMap<>();

    public static String getTablePath() {
        return tablePath;
    }

    public static String getTableName() {
        return tableName;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileRemove.getFromYandexDataBase()");

        Log.logger.info("new ActionFileRemove[]");
        ConcurrentHashMap<Integer, Remove> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String path = DataBase.getColumnString(result, "?_path".replace("?", tableName));

            Remove action = new Remove(path);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetRemove [actionId=" + actionId + ", path=" + path + "]");
        } while (result.next());

        Remove.map = actionMap;
    }

    private String path;

    public Remove(
            String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String start(String source) {
        path = path
                .replace("%s", source)
                .replace("\\", File.separator)
                .replace("/", File.separator);

        Boolean result = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                Log.logger.info("File delete: " + path);
                if (file.delete()) {
                    result = true;
                }
            } else {
                result = true;
                Log.logger.warning("No found file: " + path);
            }
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        return Boolean.toString(result);
    }

    String start() {
        String source = "";
        return this.start(source);
    }
}