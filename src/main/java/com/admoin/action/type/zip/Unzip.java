package com.admoin.action.type.zip;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;


public class Unzip implements Serializable {
    private static final long serialVersionUID = 12L;

    private static String tablePath = "action/type/zip/zip_unzip";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Unzip> map = new ConcurrentHashMap<>();

    public String getFilePath() {
        return filePath;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public String filePath;
    public String destinationDirectory;

    public Unzip(
            String filePath,
            String destinationDirectory) {
        this.filePath = filePath;
        this.destinationDirectory = destinationDirectory;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileDownload.getFromYandexDataBase()");

        Log.logger.info("new ActionFileDownload[]");
        ConcurrentHashMap<Integer, Unzip> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String filePath = DataBase.getColumnString(result, "?_file".replace("?", tableName));
            String destinationDirectory = DataBase.getColumnString(result, "?_destination_directory".replace("?", tableName));

            Unzip action = new Unzip(filePath, destinationDirectory);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetDownload [actionId=" + actionId + ", filePath=" + filePath + ", destinationDirectory=" + destinationDirectory + "]");
        } while (result.next());

        Unzip.map = actionMap;
    }

    public String start(String source) {
        filePath = filePath.replace("%s", source);
        Log.logger.info("FileOperation.unzip(" + filePath + ")");
        Boolean result = false;

        try (ZipFile zipFile = new ZipFile(filePath);) {
            zipFile.extractAll(destinationDirectory);
            result = true;
        } catch (ZipException e) {
            Log.logger.warning(e.getMessage());
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        return Boolean.toString(result);
    }
}