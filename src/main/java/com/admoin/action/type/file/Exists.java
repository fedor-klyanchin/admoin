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

public class Exists implements Serializable {
    private static final long serialVersionUID = 11L;

    private static String tablePath = "action/type/file/file_exists";
    private static String tableName = Type.getTableName(tablePath);

    private String path;

    public Exists(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ConcurrentMap<Integer, Exists> getFromDataBase() {
        Log.logger.info("ActionFileExists.getFromYandexDataBase()");

        Log.logger.info("new ActionFileExists[]");
        ConcurrentHashMap<Integer, Exists> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String path = DataBase.getColumnString(result, "?_path".replace("?", tableName));

            Exists action = new Exists(path);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", path=" + path + "]");
        } while (result.next());

        return actionMap;
    }

    public String start() {
        String source = "";
        return this.start(source);
    }

    public String start(String source) {
        path = path
                .replace("%s", source)
                .replace("\\", File.separator)
                .replace("/", File.separator);

        Boolean result = false;
        try {
            File pathFile = new File(path);

            if (pathFile.exists()) {
                Log.logger.info("File exists: " + pathFile.getAbsolutePath());
                result = true;
            } else {
                Log.logger.warning("No found file: " + pathFile.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        return Boolean.toString(result);
    }
}