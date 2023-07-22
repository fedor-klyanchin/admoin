package com.admoin.action.type.database.query;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.Action;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Upsert implements Serializable {
    private static final long serialVersionUID = 15L;

    private static String tablePath = "action/type/database/query/database_query_upsert";
    private static String tableName = Type.getTableName(tablePath);

    private String resultTablePath;

    private String resultTableName;

    public String getResultTableName() {
        return resultTableName;
    }

    public Upsert(
            String resultTablePath) {
        this.resultTablePath = resultTablePath;
        this.resultTableName = Type.getTableName(resultTablePath);
    }

    public String getResultTablePath() {
        return resultTablePath;
    }

    public static ConcurrentMap<Integer, Upsert> getFromDataBase() {
        Log.logger.info("ActionAppGetField.getFromYandexDataBase()");

        Log.logger.info("new ActionAppGetField[]");
        ConcurrentHashMap<Integer, Upsert> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String resultTablePath = DataBase.getColumnString(result, "?_table_path".replace("?", tableName));

            Upsert action = new Upsert(resultTablePath);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", resultTablePath=" + resultTablePath + "]");
        } while (result.next());

        return actionMap;
    }

    public String start(Action action) {
        String result = "false";
        if (
            !action.getResult().equals(action.getResultOld()) ||
            Boolean.TRUE.equals(!action.getSynchronizedWithDatabase())
            ) {

            String query = this.getQuery(action);
            result = Host.dataBaseReadWrite.sendQuery(query);
        }
        return result;
    }

    public String getQuery(Action action) {
        String columnNamehostId;
        
        if (resultTableName.equals("host")) {
            columnNamehostId = "host_id";
        } else {
            columnNamehostId = resultTableName + "_host_id";
        }

        return DataBase.getQueryVariableDateTimeMoscow() +
                "UPSERT INTO `" + this.resultTablePath + "` " +
                "( `" +
                columnNamehostId + "`, `" +
                resultTableName + "_datetime" + "`, `" +
                resultTableName + "_value"
                + "` ) "
                + "VALUES (" + Host.properties.getProperty("id") + ",$currentDateTimeMoscow,'" + action.getResult() + "');";
    }
}