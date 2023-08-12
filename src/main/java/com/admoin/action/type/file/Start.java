package com.admoin.action.type.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Start implements Serializable {
    private static final long serialVersionUID = 13L;

    private static String tablePath = "action/type/file/file_start";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Start> map = new ConcurrentHashMap<>();

    public static void getFromDataBase() {
        Log.logger.info("ActionFileStart.getFromYandexDataBase()");

        Log.logger.info("new ActionFileStart[]");
        ConcurrentHashMap<Integer, Start> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String command = DataBase.getColumnString(result, "?_command".replace("?", tableName));

            Start action = new Start(command);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetStart [actionId=" + actionId + ", command=" + command + "]");
        } while (result.next());

        Start.map = actionMap;
    }

    public static List<String> getListCommand(String source) {
        return Start.getListCommand(source, " ");
    }

    private static List<String> getListCommand(String source, String delimiter) {
        List<String> sourceList = new ArrayList<>();
        String sourceItem;
        int indexOf;

        do {
            indexOf = source.indexOf(delimiter);
            if (indexOf > 0) {
                sourceItem = source.substring(0, indexOf);
                sourceList.add(sourceItem);
                source = source.substring(indexOf + 1, source.length());
            } else {
                sourceList.add(source);
            }
        } while (indexOf > 0);

        return sourceList;
    }

    private String command;

    public Start(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public String start() {
        String source = "";
        return this.start(source);
    }

    public String start(String source) {
        command = command
                .replace("%s", source);

        String result;
        String resultReplace = null;
        try {
            Log.logger.info("FileOperation.start(" + command + ")");
            // https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html

            List<String> commandList = Start.getListCommand(command);

            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();

            BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int bufResult = bis.read();
            int bisAvailable = bis.available();// Исправление завершения потока в Windows 7. Иначе бесконечное ожидание заверщения
            do {
                buf.write((byte) bufResult);
                bisAvailable--;
                if (bisAvailable != -1) {
                    bufResult = bis.read();
                }
            } while (bisAvailable != -1);

            result = buf.toString("Cp866");

            process.destroy();
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
            result = e.getMessage();
        }

        if (result.isEmpty()) {
            resultReplace = "true";
        } else {
            resultReplace = result.trim();
        }

        Log.logger.info("result: " + resultReplace);
        return resultReplace;
    }
}