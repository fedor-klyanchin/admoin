package com.admoin.action.type.url.get;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class TextTest {
    String urlString = "https://www.google.com";
    String containsText = "/title";
    String delimiterFirst = ">";
    String delimiterSecond = "</title";
    Boolean useSecondDelimiterFirst = true;
    private Text action = new Text(urlString,
                    containsText,
                    delimiterFirst,
                    delimiterSecond,
                    useSecondDelimiterFirst);
    private ConcurrentHashMap<Integer, Text> actionAppGetTextMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetTextMap.put(1, action);
    }

    @Test(groups = { "Text" })
    public void newAction() {
        AssertJUnit.assertTrue(containsText.equals(action.getContainsText()));
    }

    @Test(groups = { "Text" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetTextMap.get(1).getContainsText().equals(containsText));
    }

    @Test(groups = { "Text" })
    public void getResultTrue() throws Exception {
        String report = action.start();

        AssertJUnit.assertTrue(report.equals("Google"));
    }
}
