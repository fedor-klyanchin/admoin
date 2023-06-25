package com.admoin.action.type.json.get;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class JsonTest {
    private String urlString = "http://ipinfo.io/json";
    private Json actionJsonGetJson = new Json(urlString);
    private ConcurrentHashMap<Integer, Json> actionAppGetJsonMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetJsonMap.put(1, actionJsonGetJson);
    }

    @Test(groups = { "Json" })
    public void newAction() {
        AssertJUnit.assertTrue(urlString.equals(actionJsonGetJson.getUrlString()));
    }

    @Test(groups = { "Json" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetJsonMap.get(1).getUrlString().equals(urlString));
    }

    @Test(groups = { "Json" })
    public void getResultTrue() throws IOException, URISyntaxException {
        Json testGetJsonFromUrl = new Json(urlString);
        String result = testGetJsonFromUrl.start();
        AssertJUnit.assertTrue(result.contains("https://ipinfo.io/missingauth"));
    }
}
