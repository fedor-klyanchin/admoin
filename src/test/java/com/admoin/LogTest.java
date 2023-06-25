package com.admoin;

import org.testng.AssertJUnit;
import org.testng.annotations.*;

public class LogTest {
    @BeforeClass
    public void setUp() throws Exception {
        // code that will be invoked when this test is instantiated
        Log.create();
    }

    @Test(groups = { "Log" })
    public void hostNew() throws Exception {
        AssertJUnit.assertTrue(Log.logName.equals("Log"));
    }

}
