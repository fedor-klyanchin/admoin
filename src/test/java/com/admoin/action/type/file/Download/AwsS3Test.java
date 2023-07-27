package com.admoin.action.type.file.Download;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.file.download.AwsS3;
import com.admoin.action.type.zip.Unzip;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class AwsS3Test {
    private String pathFileZip = System.getProperty("user.dir") + File.separator
            + "testDownloadObjectFromPrivateStorage.zip";
    private String pathTestFile = System.getProperty("user.dir") + File.separator + "testDownloadObjectFromPrivateStorage"
            + File.separator + "test.txt";

    private File fileZip = new File(pathFileZip);
    private File fileTestFile = new File(pathTestFile);
    private File fileTestDirectory = new File(fileTestFile.getParentFile().getAbsolutePath());

    private String bucketName;
    private String objectName;
    private String objectLocalPath;
    private String accessKey;
    private String secretKey;
    private AwsS3 actionStringCompareDownload;

    private ConcurrentHashMap<Integer, AwsS3> actionAppGetDownloadMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();

        Gson gson = new Gson();
        Map<String, String> configBucketTest = new HashMap<>();     

        JsonReader reader = new JsonReader(new FileReader("config-bucket-test.json"));
        configBucketTest = gson.fromJson(reader, configBucketTest.getClass());

        bucketName = configBucketTest.get("bucket_name");
        objectName = configBucketTest.get("object_name");
        objectLocalPath = fileZip.getAbsolutePath();
        accessKey = configBucketTest.get("access_key");
        secretKey = configBucketTest.get("secret_key");
        
        actionStringCompareDownload = new AwsS3(
                        bucketName,
                        objectName,
                        objectLocalPath,
                        accessKey,
                        secretKey);
                        
        actionAppGetDownloadMap.put(1, actionStringCompareDownload);
    }

    @Test(groups = { "Download" })
    public void newAction() {
        AssertJUnit.assertTrue(bucketName.equals(actionStringCompareDownload.getBucketName()));
    }

    @Test(groups = { "Download" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetDownloadMap.get(1).getBucketName().equals(bucketName));
    }

    @Test(groups = { "Download" })
    public void getResultTrue() throws IOException {
        fileZip.delete();
        fileTestFile.delete();
        fileTestDirectory.delete();

        AwsS3 testDownloadObjectFromPrivateStorage = new AwsS3(
                bucketName,
                objectName,
                objectLocalPath,
                accessKey,
                secretKey);

        if (fileZip.getParentFile().exists()) {
            testDownloadObjectFromPrivateStorage.start("");
        }

        if (fileZip.exists()) {
            Unzip unzipAction = new Unzip(
                    pathFileZip,
                    fileZip.getParentFile().getAbsolutePath());
            unzipAction.start("");
        }

        AssertJUnit.assertTrue(fileTestFile.exists());

        fileTestFile.delete();
        fileTestDirectory.delete();
        fileZip.delete();
    }
}
