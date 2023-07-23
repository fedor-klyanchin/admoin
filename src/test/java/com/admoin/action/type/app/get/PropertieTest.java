package com.admoin.action.type.app.get;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.App;
import com.admoin.Host;
import com.admoin.Log;

public class PropertieTest {
    private String fieldName = "app_version";
    private Propertie actionStringComparePropertie = new Propertie(fieldName);
    private ConcurrentHashMap<Integer, Propertie> actionAppGetPropertieMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetPropertieMap.put(1, actionStringComparePropertie);
        Host.setProperty(App.getAppVersionPropertyName(), App.getAppVersion());
    }

    @Test(groups = { "Propertie" })
    public void newAction() {
        AssertJUnit.assertTrue(fieldName.equals(actionStringComparePropertie.getPropertieName()));
    }

    @Test(groups = { "Propertie" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetPropertieMap.get(1).getPropertieName().equals(fieldName));
    }

    @Test(groups = { "Propertie" })
    public void getResultTrue() {
        AssertJUnit.assertTrue(App.APP_VERSION.equals(actionAppGetPropertieMap.get(1).start()));
    }

    @Test(groups = { "Propertie" })
    public void getResultFalse() {
        String testValue = "0.7.4";
        AssertJUnit.assertFalse(testValue.equals(actionAppGetPropertieMap.get(1).start()));
    }
}
