package com.admoin;

import org.testng.AssertJUnit;
import org.testng.annotations.*;

public class AppTest {
    @BeforeClass
    public void setUp() throws Exception {
        // code that will be invoked when this test is instantiated
        Log.create();
        Host.getProperties();
        Host host = new Host();
        host.restoreFromLocalFile();
        host.id = Integer.parseInt(Host.properties.getProperty("id"));
        Host.setProperty(App.getAppVersionPropertyName(), App.APP_VERSION);
    }

    @Test(groups = { "App" })
    public void isRetryExecution() throws Exception {
        AssertJUnit.assertTrue(
                !App.isRetryExecution() ||
                        (App.isRetryExecution() &&
                                Host.config != null &&
                                !App.oldVersionApp &&
                                !App.isUseLotMemory() &&
                                !App.exitApp));
    }

}
