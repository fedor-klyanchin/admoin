package com.admoin.action.type.file.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class PublicFile implements Serializable {
    private static final long serialVersionUID = 12L;

    private static String tablePath = "action/type/file/download/file_download_public_file";
    private static String tableName = Type.getTableName(tablePath);

    private String localPath;
    private String remotePath;

    public PublicFile(
            String localPath,
            String remotePath) {
        this.localPath = localPath;
        this.remotePath = remotePath;
    }

    public static ConcurrentMap<Integer, PublicFile> getFromDataBase() {
        Log.logger.info("ActionFileDownload.getFromYandexDataBase()");

        Log.logger.info("new ActionFileDownload[]");
        ConcurrentHashMap<Integer, PublicFile> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String localPath = DataBase.getColumnString(result, "?_local_path".replace("?", tableName));
            String remotePath = DataBase.getColumnString(result, "?_remote_path".replace("?", tableName));

            PublicFile actionAppGetDownload = new PublicFile(localPath, remotePath);

            actionMap.put(actionId, actionAppGetDownload);

            Log.logger.info("New actionAppGetDownload [actionId=" + actionId + ", localPath=" + localPath + ", remotePath=" + remotePath + "]");
        } while (result.next());
        return actionMap;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public String start(String source) {
        localPath = localPath
                .replace("%s", source)
                .replace("\\", File.separator)
                .replace("/", File.separator);

        remotePath = remotePath
                .replace("%s", source);

        Log.logger.info("FileOperation.download(" + remotePath + ", " + localPath + ")");
        Boolean result = false;
        Path path = FileSystems.getDefault().getPath(localPath);

        try {
            URI url = URI.create(remotePath);
            InputStream in = url.toURL().openStream();

            try {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                File file = new File(localPath);
                result = file.exists();
            } catch (FileAlreadyExistsException e) {
                Log.logger.warning("File of that name already exists");
            } catch (DirectoryNotEmptyException e) {
                Log.logger.warning("Non-empty directory");
            } catch (UnsupportedOperationException e) {
                Log.logger.warning("Contains a copy option that is not supported");
            } catch (SecurityException e) {
                Log.logger.warning("Write access denied");
            } catch (IOException e) {
                // Log.logger.warning("Unable to copy: " + url + ": " + url + path);
                Log.logger.warning("Unable to copy: %s: %s%n".replace("%s", url.toString()).replace("%n", path.toString()));
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
            }
        } catch (MalformedURLException e) {
            Log.logger.warning("new URL() failed");
        } catch (IOException e) {
            Log.logger.warning("I/O error reading or writing");
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        return Boolean.toString(result);
    }
}