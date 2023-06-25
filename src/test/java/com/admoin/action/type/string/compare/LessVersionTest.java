package com.admoin.action.type.string.compare;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class LessVersionTest {
    private String controlValue = "0.1.1";
    private LessVersion actionStringCompareLessVersion = new LessVersion(controlValue);
    private ConcurrentHashMap<Integer, LessVersion> actionStringCompareLessVersionMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionStringCompareLessVersionMap.put(1, actionStringCompareLessVersion);
    }

    @Test(groups = { "LessVersion" })
    public void newAction() {
        AssertJUnit.assertTrue(controlValue.equals(actionStringCompareLessVersion.getControlValue()));
    }

    @Test(groups = { "LessVersion" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionStringCompareLessVersionMap.get(1).getControlValue().equals(controlValue));
    }

    @Test(groups = { "LessVersion" })
    public void getResultTrue() {
        AssertJUnit.assertTrue(
                LessVersion.isLessVersion("0.1.0", "0.2.0") &&
                        LessVersion.isLessVersion("102.0.1", "102.10.0") &&
                        LessVersion.isLessVersion("112.0.1", "") &&
                        LessVersion.isLessVersion("", "102.10.0") &&
                        LessVersion.isLessVersion("2.0.0.28 (e9eb91ecb)", "2.0.0.29 (e9eb91ecb)") &&
                        LessVersion.isLessVersion("8.3.21.1622", "8.3.21.1623") &&
                        LessVersion.isLessVersion("17.01 beta", "22.01") &&
                        LessVersion.isLessVersion("0.0.0.0", "0.0.0.1") &&
                        LessVersion.isLessVersion("23.001.20143", "23.011.20143") &&
                        LessVersion.isLessVersion("112.0.5615.138", "112.0.5625.138") &&
                        LessVersion.isLessVersion("1.3.3.10", "1.3.4.10") &&
                        LessVersion.isLessVersion("112.0.1", "112.0.2") &&
                        LessVersion.isLessVersion("7.0.1.2", "7.5.2.2") &&
                        LessVersion.isLessVersion("102.10.0", "112.0.2") &&
                        LessVersion.isLessVersion("3.0.18", "4.0.18"));
    }

    @Test(groups = { "LessVersion" })
    public void getResultFalse() {
        AssertJUnit.assertFalse(
                LessVersion.isLessVersion("0.2.0", "0.1.0") &&
                        LessVersion.isLessVersion("", "") &&
                        LessVersion.isLessVersion("2.0.0.35 (e9eb91ecb)", "2.0.0.29 (e9eb91ecb)") &&
                        LessVersion.isLessVersion("8.3.21.1625", "8.3.21.1623") &&
                        LessVersion.isLessVersion("27.01 beta", "22.01") &&
                        LessVersion.isLessVersion("0.0.0.2", "0.0.0.1") &&
                        LessVersion.isLessVersion("43.001.20143", "23.011.20143") &&
                        LessVersion.isLessVersion("112.0.7615.138", "112.0.5625.138") &&
                        LessVersion.isLessVersion("1.3.5.10", "1.3.4.10") &&
                        LessVersion.isLessVersion("152.0.1", "112.0.2") &&
                        LessVersion.isLessVersion("7.7.1.2", "7.5.2.2") &&
                        LessVersion.isLessVersion("122.10.0", "112.0.2") &&
                        LessVersion.isLessVersion("5.0.18", "4.0.18"));
    }
}
