package com.admoin;

import org.testng.AssertJUnit;
import org.testng.annotations.*;

public class AppTest {
    Host host;

    @BeforeClass
    public void setUp() throws Exception {
        // code that will be invoked when this test is instantiated
        Log.create();
        Host.getProperties();
        host = Host.restoreFromLocalFile();
        host.id = Integer.parseInt(Host.properties.getProperty("id"));
        Host.setProperty(App.getAppVersionPropertyName(), App.APP_VERSION);
    }

    @Test(groups = { "App" })
    public void testIsRetryExecution() throws Exception {
        AssertJUnit.assertTrue(
                !App.isRetryExecution() ||
                        (App.isRetryExecution() &&
                                host.getConfig() != null &&
                                !App.oldVersionApp &&
                                !App.isUseLotMemory() &&
                                !App.exitApp));
    }

    @Test(groups = { "App" })
    public void testTrueIsDatabaseConnectionStringChanged() throws Exception {
        String dataBaseReadOnlyConnectionString = "1";
        AssertJUnit.assertFalse(
            !dataBaseReadOnlyConnectionString.equals(Host.properties.getProperty("yandex_data_base_read_only_connection_string_test","1"))
        );
    }

    @Test(groups = { "App" })
    public void testFalseIsDatabaseConnectionStringChanged() throws Exception {
        String dataBaseReadOnlyConnectionString = "2";
        AssertJUnit.assertTrue(
            !dataBaseReadOnlyConnectionString.equals(Host.properties.getProperty("yandex_data_base_read_only_connection_string_test","1"))
        );
    }
}
