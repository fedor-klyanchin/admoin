package com.admoin.action.type.json.get;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class ValueTest {
    private String jsonString = "{  \"name1\": \"value1\",  \"name2\": \"value2\",  \"name3\": \"value3\"}";
    private Value actionJsonGetValue = new Value(jsonString);
    private ConcurrentHashMap<Integer, Value> actionAppGetValueMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetValueMap.put(1, actionJsonGetValue);
    }

    @Test(groups = { "Value" })
    public void newAction() {
        AssertJUnit.assertTrue(jsonString.equals(actionJsonGetValue.getName()));
    }

    @Test(groups = { "Value" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetValueMap.get(1).getName().equals(jsonString));
    }

    @Test(groups = { "Value" })
    public void getResultTrue() throws IOException {
        Value testGetJsonElementValueByName = new Value("name2");
        String result = testGetJsonElementValueByName.start(jsonString);
        AssertJUnit.assertTrue(result.equals("value2"));
    }
}
