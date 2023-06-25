package com.admoin.action.type.app.get;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.App;
import com.admoin.Host;
import com.admoin.Log;

public class FieldTest {
    private String fieldName = "appVersion";
    private Field actionStringCompareField = new Field(fieldName);
    private ConcurrentHashMap<Integer, Field> actionAppGetFieldMap = new ConcurrentHashMap<>();
    private Host host;

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        host = new Host();
        actionAppGetFieldMap.put(1, actionStringCompareField);
    }

    @Test(groups = { "Field" })
    public void newAction() {
        AssertJUnit.assertTrue(fieldName.equals(actionStringCompareField.getFieldName()));
    }

    @Test(groups = { "Field" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetFieldMap.get(1).getFieldName().equals(fieldName));
    }

    @Test(groups = { "Field" })
    public void getResultTrue() {
        AssertJUnit.assertTrue(App.APP_VERSION.equals(actionAppGetFieldMap.get(1).start(host)));
    }

    @Test(groups = { "Field" })
    public void getResultFalse() {
        String testValue = "0";
        AssertJUnit.assertFalse(testValue.equals(actionAppGetFieldMap.get(1).start(host)));
    }
}
