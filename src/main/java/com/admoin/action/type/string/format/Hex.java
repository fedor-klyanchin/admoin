package com.admoin.action.type.string.format;

import java.io.Serializable;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Hex implements Serializable {
    private static final long serialVersionUID = 19L;

    private static String tablePath = "action/type/string/format/hex";
    private static String tableName = Type.getTableName(tablePath);

    public String string;

    public Hex(
            String string) {
        this.string = string;
    }

    public static ConcurrentMap<Integer, Hex> getFromDataBase() {
        Log.logger.info("ActionFileHex.getFromYandexDataBase()");

        Log.logger.info("new ActionFileHex[]");
        ConcurrentHashMap<Integer, Hex> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String string = DataBase.getColumnString(result, "?_string".replace("?", tableName));

            Hex action = new Hex(string);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetHex [actionId=" + actionId + ", string=" + string + "]");
        } while (result.next());

        return actionMap;
    }

    public String start(String source) {
        Log.logger.info("formatStringToHex.start(" + string + ")");

        String result = "";

        try {
            HexFormat hexFormat = HexFormat.of();
            byte[] bytes = source.getBytes();
            result = hexFormat.formatHex(bytes);
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
        return result;
    }

    public String getString() {
        return string;
    }
}