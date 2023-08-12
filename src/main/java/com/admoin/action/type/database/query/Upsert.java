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

    public static ConcurrentMap<Integer, Upsert> map = new ConcurrentHashMap<>();

    private int actionIdResult;
    private String resultTablePath;
    private String resultTableName;
    private String resultTargetAction;

    public int getActionIdResult() {
        return actionIdResult;
    }

    public void setActionIdResult(int actionIdResult) {
        this.actionIdResult = actionIdResult;
    }

    public String getResultTargetAction() {
        return resultTargetAction;
    }

    public String getResultTableName() {
        return resultTableName;
    }

    public void setResultTargetAction(String resultTargetAction) {
        this.resultTargetAction = resultTargetAction;
    }

    public Upsert(int actionIdResult, 
            String resultTablePath) {
        this.actionIdResult = actionIdResult;
        this.resultTablePath = resultTablePath;
        this.resultTableName = Type.getTableName(resultTablePath);
    }

    public String getResultTablePath() {
        return resultTablePath;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionAppGetField.getFromYandexDataBase()");

        Log.logger.info("new ActionAppGetField[]");
        ConcurrentHashMap<Integer, Upsert> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            int actionIdResult = DataBase.getColumnInt(result, "?_action_id_result".replace("?", tableName));
            String resultTablePath = DataBase.getColumnString(result, "?_table_path".replace("?", tableName));

            Upsert action = new Upsert(actionIdResult, resultTablePath);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", resultTablePath=" + resultTablePath + "]");
        } while (result.next());

        Upsert.map = actionMap;
    }

    public String start() {
        String result = "false";
        Action action = Action.map.get(actionIdResult);
        setResultTargetAction(action.getResult());

        Boolean isChangeResult = !action.getResult().equals(action.getResultOld());
        Boolean isNotSynchronizedWithDatabase = Boolean.TRUE.equals(!action.getSynchronizedWithDatabase());

        if (isChangeResult || isNotSynchronizedWithDatabase) {

            String query = this.getQuery();
            result = Host.dataBaseReadWrite.sendQuery(query);
        } else {
            result = "true";
        }
        return result;
    }

    public String getQuery() {
        String columnNamehostId;
        
        if (resultTableName.equals("host")) {
            columnNamehostId = "host_id";
        } else {
            columnNamehostId = resultTableName + "_host_id";
        }

        return "UPSERT INTO `" + this.resultTablePath + "` " +
                "( `" +
                columnNamehostId + "`, `" +
                resultTableName + "_datetime" + "`, `" +
                resultTableName + "_value"
                + "` ) "
                + "VALUES (" + Host.properties.getProperty("id") + ",CurrentUtcDatetime(),'" + resultTargetAction + "');";
    }

    public String getResult(Action action) {
        return action.getResult();
    }
}