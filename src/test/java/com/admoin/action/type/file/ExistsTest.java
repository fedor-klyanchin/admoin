package com.admoin.action.type.file;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class ExistsTest {
    private String path = "pom.xml";
    private Exists actionStringCompareExists = new Exists(path);
    private ConcurrentHashMap<Integer, Exists> actionAppGetExistsMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetExistsMap.put(1, actionStringCompareExists);
    }

    @Test(groups = { "Exists" })
    public void newAction() {
        AssertJUnit.assertTrue(path.equals(actionStringCompareExists.getPath()));
    }

    @Test(groups = { "Exists" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetExistsMap.get(1).getPath().equals(path));
    }

    @Test(groups = { "Exists" })
    public void getResultTrue() throws IOException {
        Exists testFileExists = new Exists(path);
        String result = testFileExists.start();
        AssertJUnit.assertTrue(result.equals("true"));
    }
}
