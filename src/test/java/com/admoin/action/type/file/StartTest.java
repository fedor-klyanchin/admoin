package com.admoin.action.type.file;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class StartTest {
    private String path = "ping %s -c 1";
    private Start actionStringCompareStart = new Start(path);
    private ConcurrentHashMap<Integer, Start> actionAppGetStartMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetStartMap.put(1, actionStringCompareStart);
    }

    @Test(groups = { "Start" })
    public void newAction() {
        AssertJUnit.assertTrue(path.equals(actionStringCompareStart.getCommand()));
    }

    @Test(groups = { "Start" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetStartMap.get(1).getCommand().equals(path));
    }

    @Test(groups = { "Start" })
    public void getResultTrue() throws IOException {
        String command = "ping %s -c 1";
        String source = "8.8.8.8";
        Start testStartCommand = new Start(command);
        String result = testStartCommand.start(source);
        AssertJUnit.assertTrue(!result.isEmpty());
    }
}
