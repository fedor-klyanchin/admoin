package com.admoin.action.type.file;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class RemoveTest {
    private String path = "testDeleteFile.txt";
    private Remove actionStringCompareRemove = new Remove(path);
    private ConcurrentHashMap<Integer, Remove> actionAppGetRemoveMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetRemoveMap.put(1, actionStringCompareRemove);
    }

    @Test(groups = { "Remove" })
    public void newAction() {
        AssertJUnit.assertTrue(path.equals(actionStringCompareRemove.getPath()));
    }

    @Test(groups = { "Remove" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetRemoveMap.get(1).getPath().equals(path));
    }

    @Test(groups = { "Remove" })
    public void getResultTrue() throws IOException {
        String pathFile = System.getProperty("user.dir") + File.separator + path;

        File testFile = new File(pathFile);
        testFile.getParentFile().mkdirs();

        if (!testFile.exists()) {
            testFile.createNewFile();
        }

        Remove actionDeleteFile = null;
        if (testFile.exists()) {
            actionDeleteFile = new Remove(pathFile);
        }

        AssertJUnit.assertTrue(
                actionDeleteFile != null &&
                        actionDeleteFile.start("").equals("true") &&
                        !testFile.exists());
    }
}
