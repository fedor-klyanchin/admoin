package com.admoin.action.type.file.Download;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.file.Remove;
import com.admoin.action.type.file.download.PublicFile;

public class PublicFileTest {
    private String localPath = System.getProperty("user.dir") + File.separator + "connecttest.txt";
    private String remotePath = "http://www.msftconnecttest.com/connecttest.txt";
    private PublicFile actionStringCompareDownload = new PublicFile(localPath, remotePath);
    private ConcurrentHashMap<Integer, PublicFile> actionAppGetDownloadMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetDownloadMap.put(1, actionStringCompareDownload);
    }

    @Test(groups = { "Download" })
    public void newAction() {
        AssertJUnit.assertTrue(localPath.equals(actionStringCompareDownload.getLocalPath()));
    }

    @Test(groups = { "Download" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetDownloadMap.get(1).getLocalPath().equals(localPath));
    }

    @Test(groups = { "Download" })
    public void getResultTrue() throws IOException {
        String result = "false";

        File testFile = new File(localPath);
        testFile.getParentFile().mkdirs();

        if (testFile.exists()) {
            Remove actionDeleteFile = new Remove(localPath);
            actionDeleteFile.start("");
        }

        PublicFile actionDownloadFile = new PublicFile(localPath, remotePath);
        result = actionDownloadFile.start("");
        // Action.downloadFile(remotePath, localPath);

        AssertJUnit.assertTrue(
                result.equals("true") &&
                        testFile.exists());

        testFile.delete();
    }
}
