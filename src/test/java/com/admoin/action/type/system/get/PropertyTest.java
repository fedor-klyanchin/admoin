package com.admoin.action.type.system.get;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class PropertyTest {
    private String property = "file.separator";
    private Property actionJsonGetProperty = new Property(property);
    private ConcurrentHashMap<Integer, Property> actionAppGetPropertyMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetPropertyMap.put(1, actionJsonGetProperty);
    }

    @Test(groups = { "Property" })
    public void newAction() {
        AssertJUnit.assertTrue(property.equals(actionJsonGetProperty.getName()));
    }

    @Test(groups = { "Property" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetPropertyMap.get(1).getName().equals(property));
    }

    @Test(groups = { "Property" })
    public void getResultTrue() throws IOException {
        Property testGetSystemProperty = new Property(property);
        String result = testGetSystemProperty.start();
        AssertJUnit.assertTrue(result.equals(System.getProperty(property)));
    }
}
