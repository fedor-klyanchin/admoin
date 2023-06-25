package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class LessTest {
    private String controlValue = "Windows 10";
    private Less actionStringCompareLess = new Less(controlValue);
    private ConcurrentHashMap<Integer, Less> actionStringCompareLessMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionStringCompareLessMap.put(1, actionStringCompareLess);
    }

    @Test(groups = { "Less" })
    public void newAction() {
        AssertJUnit.assertTrue(controlValue.equals(actionStringCompareLess.getControlValue()));
    }

    @Test(groups = { "Less" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionStringCompareLessMap.get(1).getControlValue().equals(controlValue));
    }

    @Test(groups = { "Less" })
    public void getResultTrue() {
        String source = "Windows";
        AssertJUnit.assertTrue(Boolean.parseBoolean(actionStringCompareLessMap.get(1).start(source)));
    }

    @Test(groups = { "Less" })
    public void getResultFalse() {
        String source = "WindowsWindows";
        AssertJUnit.assertTrue(actionStringCompareLessMap.get(1).start(source).equals("false"));
    }
}
