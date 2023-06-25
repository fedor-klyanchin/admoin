package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class ContainsTest {
    private String controlValue = "Windows";
    private Contains actionStringCompareContains = new Contains(controlValue);
    private ConcurrentHashMap<Integer, Contains> actionStringCompareContainsMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionStringCompareContainsMap.put(1, actionStringCompareContains);
    }

    @Test(groups = { "Contains" })
    public void newAction() {
        AssertJUnit.assertTrue(controlValue.equals(actionStringCompareContains.getControlValue()));
    }

    @Test(groups = { "Contains" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionStringCompareContainsMap.get(1).getControlValue().equals(controlValue));
    }

    @Test(groups = { "Contains" })
    public void getResultTrue() {
        String source = "Windows 10";
        AssertJUnit.assertTrue(Boolean.parseBoolean(actionStringCompareContainsMap.get(1).start(source)));
    }

    @Test(groups = { "Contains" })
    public void getResultFalse() {
        String source = "Ubuntu";
        AssertJUnit.assertFalse(Boolean.parseBoolean(actionStringCompareContainsMap.get(1).start(source)));
    }
}
