package com.admoin.action.type.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import java.io.FileOutputStream;

import tech.ydb.table.result.ResultSetReader;

public class Unzip implements Serializable {
    private static final long serialVersionUID = 12L;

    private static String tablePath = "action/type/zip/zip_unzip";
    private static String tableName = Type.getTableName(tablePath);

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

    public static ConcurrentMap<Integer, Unzip> getFromDataBase() {
        Log.logger.info("ActionFileDownload.getFromYandexDataBase()");

        Log.logger.info("new ActionFileDownload[]");
        ConcurrentHashMap<Integer, Unzip> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String filePath = DataBase.getColumnString(result, "?_file".replace("?", tableName));
            String destinationDirectory = DataBase.getColumnString(result, "?_destination_directory".replace("?", tableName));

            Unzip action = new Unzip(filePath, destinationDirectory);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetDownload [actionId=" + actionId + ", filePath=" + filePath + ", destinationDirectory=" + destinationDirectory + "]");
        } while (result.next());

        return actionMap;
    }

    public String start(String source) {
        filePath = filePath.replace("%s", source);
        Log.logger.info("FileOperation.unzip(" + filePath + ")");
        Boolean result = false;

        File zipFile = new File(filePath);
        if (!zipFile.exists()) {
            Log.logger.warning("No found file: filePath");
        } else {
            String testDirectory;
            final int BUFFER_SIZE = 2048;

            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdir();
            }

            ZipInputStream zipIn;
            try {
                zipIn = new ZipInputStream(new FileInputStream(filePath));

                ZipEntry zipEntry;
                try {
                    zipEntry = zipIn.getNextEntry();

                    while (zipEntry != null) {
                        File file = new File(destinationDirectory + File.separator + zipEntry.getName());

                        if (zipEntry.getSize() == 0) {
                            File dir = new File(file.getAbsolutePath());
                            if (!dir.exists()) {// Fix. isDirectory not work
                                dir.mkdir();
                            }
                        }

                        testDirectory = file.getAbsolutePath().substring(0,
                                file.getAbsolutePath().lastIndexOf(File.separator));
                        while (testDirectory != null) {
                            File testDirectoryCheck = new File(testDirectory);
                            if (!testDirectoryCheck.exists()) {
                                testDirectoryCheck.mkdir();
                            }

                            if (testDirectory.contains(File.separator)) {
                                testDirectory = testDirectory.substring(0, testDirectory.lastIndexOf(File.separator));
                            } else {
                                testDirectory = null;
                            }
                        }

                        if (zipEntry.getSize() != 0) {
                            if (!file.toPath().normalize().startsWith(zipFile.getParent())) {
                                throw new Exception("Bad zip entry");
                            }
                            
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                    new FileOutputStream(file.getAbsolutePath()));
                            byte[] bytesIn = new byte[BUFFER_SIZE];
                            int read = 0;
                            while ((read = zipIn.read(bytesIn)) != -1) {
                                bufferedOutputStream.write(bytesIn, 0, read);
                            }
                            bufferedOutputStream.close();
                        }

                        zipIn.closeEntry();
                        zipEntry = zipIn.getNextEntry();
                    }
                    zipIn.close();
                    result = true;
                } catch (IOException e) {
                    Log.logger.warning(e.getMessage());
                } catch (Exception e) {
                    Log.logger.warning(e.getMessage());
                }
            } catch (FileNotFoundException e) {
                Log.logger.warning(e.getMessage());
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
            }
        }

        return Boolean.toString(result);
    }
}