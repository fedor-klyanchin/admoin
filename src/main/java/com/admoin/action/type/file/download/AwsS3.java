package com.admoin.action.type.file.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import tech.ydb.table.result.ResultSetReader;

public class AwsS3 implements Serializable {
    private static final long serialVersionUID = 9L;

    private static String tablePath = "action/type/file/download/file_download_aws_s3";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, AwsS3> map = new ConcurrentHashMap<>();

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getObjectLocalPath() {
        return objectLocalPath;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    private String bucketName;
    private String objectName;
    private String objectLocalPath;
    private String accessKey;
    private String secretKey;

    public AwsS3(
            String bucketName,
            String objectName,
            String objectLocalPath,
            String accessKey,
            String secretKey) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.objectLocalPath = objectLocalPath;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileDownload.getFromYandexDataBase()");

        Log.logger.info("new ActionFileDownload[]");
        ConcurrentMap<Integer, AwsS3> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String bucketName = DataBase.getColumnString(result, "?_bucket_name".replace("?", tableName));
            String objectName = DataBase.getColumnString(result, "?_object_name".replace("?", tableName));
            String objectLocalPath = DataBase.getColumnString(result, "?_object_local_path".replace("?", tableName));
            String accessKey = DataBase.getColumnString(result, "?_access_key".replace("?", tableName));
            String secretKey = DataBase.getColumnString(result, "?_secret_key".replace("?", tableName));

            AwsS3 actionAppGetDownload = new AwsS3(
                    bucketName,
                    objectName,
                    objectLocalPath,
                    accessKey,
                    secretKey);

            actionMap.put(actionId, actionAppGetDownload);

            Log.logger.info("New DownloadObjectFromPrivateStorage. actionId: " + actionId + " objectName: " + objectName
                    + " objectLocalPath: " + objectLocalPath);
        } while (result.next());
        
        AwsS3.map = actionMap;
    }

    String start() throws Exception {
        String source = "";
        return this.start(source);
    }

    public String start(String source) throws IOException {
        objectName = objectName.replace("%s", source);
        objectLocalPath = objectLocalPath.replace("%s", source);

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"))
                .build();

        try {
            S3Object o = s3Client.getObject(bucketName, objectName);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(objectLocalPath));
            byte[] readBuf = new byte[1024];
            int readLen = 0;
            while ((readLen = s3is.read(readBuf)) > 0) {
                fos.write(readBuf, 0, readLen);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            Log.logger.warning(e.getErrorMessage());
        } catch (IOException e) {
            Log.logger.warning(e.getMessage());
        } 

        File storageObjectFile = new File(objectLocalPath);
        return Boolean.toString(storageObjectFile.exists());
    }
}