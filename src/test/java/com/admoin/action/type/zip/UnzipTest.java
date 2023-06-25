package com.admoin.action.type.zip;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.file.download.PublicFile;

public class UnzipTest {
        String destinationDirectory = "https://storage.yandexcloud.net/for-tests-do-not-delete-public/testDownloadObjectFromPrivateStorage.zip";
        String pathFileZip = System.getProperty("user.dir") + File.separator
                + "testDownloadObjectFromPrivateStorage.zip";
        String pathTestFile = System.getProperty("user.dir") + File.separator + "testDownloadObjectFromPrivateStorage"
                + File.separator + "test.txt";
    private Unzip actionStringCompareDownload = new Unzip(pathFileZip, destinationDirectory);
    private ConcurrentHashMap<Integer, Unzip> actionAppGetDownloadMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetDownloadMap.put(1, actionStringCompareDownload);
    }

    @Test(groups = { "Download" })
    public void newAction() {
        AssertJUnit.assertTrue(destinationDirectory.equals(actionStringCompareDownload.getDestinationDirectory()));
    }

    @Test(groups = { "Download" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetDownloadMap.get(1).getDestinationDirectory().equals(destinationDirectory));
    }

    @Test(groups = { "Download" })
    public void getResultTrue() throws IOException {
        String result;

        File fileZip = new File(pathFileZip);
        File fileTestFile = new File(pathTestFile);
        File fileTestDirectory = new File(fileTestFile.getParentFile().getAbsolutePath());

        fileZip.delete();
        fileTestFile.delete();
        fileTestDirectory.delete();

        PublicFile actionDownloadFile = new PublicFile(pathFileZip, destinationDirectory);
        result = actionDownloadFile.start("");

        if (fileZip.getParentFile().exists()) {
            actionDownloadFile.start("");
        }

        if (fileZip.exists()) {
            Unzip unzipAction = new Unzip(
                    pathFileZip,
                    fileZip.getParentFile().getAbsolutePath());
            unzipAction.start("");
        }

        AssertJUnit.assertTrue(result.equals("true") && fileTestFile.exists());

        fileTestFile.delete();
        fileTestDirectory.delete();
        fileZip.delete();
    }
}
