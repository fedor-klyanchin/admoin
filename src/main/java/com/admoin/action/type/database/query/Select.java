package com.admoin.action.type.database.query;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Select implements Serializable {
    private static final long serialVersionUID = 15L;

    private static String tablePath = "action/type/database/query/select";
    private static String tableName = Type.getTableName(tablePath);

    private String resultTablePath;
    private String resultTableName;

    public String getResultTableName() {
        return resultTableName;
    }

    public Select(
            String resultTablePath) {
        this.resultTablePath = resultTablePath;
        this.resultTableName = Type.getTableName(resultTablePath);
    }

    public String getResultTablePath() {
        return resultTablePath;
    }

    public static ConcurrentMap<Integer, Select> getFromDataBase() {
        Log.logger.info("ActionAppGetField.getFromYandexDataBase()");

        Log.logger.info("new ActionAppGetField[]");
        ConcurrentHashMap<Integer, Select> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String resultTablePath = DataBase.getColumnString(result, "?_table_path".replace("?", tableName));

            Select action = new Select(resultTablePath);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", resultTablePath=" + resultTablePath + "]");
        } while (result.next());

        return actionMap;
    }

    public String start() {
        String result;
        String columnNamehostId;

        if (resultTableName.equals("host")) {
            columnNamehostId = "host_id";
        } else {
            columnNamehostId = resultTableName + "_host_id";
        }

        String query = "SELECT `" + resultTableName + "_value` "
                + "FROM `" + resultTablePath + "` "
                + "WHERE `" + columnNamehostId + "` == " + Host.properties.getProperty("id") + ";";

        ResultSetReader resultQuery = Host.dataBaseReadWrite.getQuery(query);
        resultQuery.next();
        String columnNameValue = resultTableName + "_value";
        result = resultQuery.getColumn(columnNameValue).getText();

        return result;
    }
}