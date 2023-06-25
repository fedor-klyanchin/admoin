package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class EqualsTest {
    private String controlValue = "Windows";
    private Equals actionStringCompareEquals = new Equals(controlValue);
    private ConcurrentHashMap<Integer, Equals> actionStringCompareEqualsMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionStringCompareEqualsMap.put(1, actionStringCompareEquals);
    }

    @Test(groups = { "Equals" })
    public void newAction() {
        AssertJUnit.assertTrue(controlValue.equals(actionStringCompareEquals.getControlValue()));
    }

    @Test(groups = { "Equals" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionStringCompareEqualsMap.get(1).getControlValue().equals(controlValue));
    }

    @Test(groups = { "Equals" })
    public void getResultTrue() {
        String source = "Windows";
        AssertJUnit.assertTrue(Boolean.parseBoolean(actionStringCompareEqualsMap.get(1).start(source)));
    }

    @Test(groups = { "Equals" })
    public void getResultFalse() {
        String source = "Ubuntu";
        AssertJUnit.assertFalse(Boolean.parseBoolean(actionStringCompareEqualsMap.get(1).start(source)));
    }
}
