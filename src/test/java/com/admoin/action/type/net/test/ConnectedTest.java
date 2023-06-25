package com.admoin.action.type.net.test;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class ConnectedTest {
    private String yandexDataBaseServerlessNameString;
    private String yandexDataBaseServerlessPortString;
    private int yandexDataBaseServerlessPort;
    private Connection actionStringCompareDownload;
    private ConcurrentHashMap<Integer, Connection> actionAppGetDownloadMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        yandexDataBaseServerlessNameString = Host.properties.getProperty("yandex_data_base_serverless_name");
        yandexDataBaseServerlessPortString = Host.properties.getProperty("yandex_data_base_serverless_port");
        yandexDataBaseServerlessPort = Integer.parseInt(yandexDataBaseServerlessPortString);
        actionStringCompareDownload = new Connection(yandexDataBaseServerlessNameString, yandexDataBaseServerlessPort);
        actionAppGetDownloadMap.put(1, actionStringCompareDownload);
    }

    @Test(groups = { "Download" })
    public void newAction() {
        AssertJUnit.assertTrue(yandexDataBaseServerlessNameString.equals(actionStringCompareDownload.getAddress()));
    }

    @Test(groups = { "Download" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetDownloadMap.get(1).getAddress().equals(yandexDataBaseServerlessNameString));
    }

    @Test(groups = { "Download" })
    public void getResultTrue() throws IOException {
        Connection testIsConnected = new Connection(yandexDataBaseServerlessNameString,
                yandexDataBaseServerlessPort);
        AssertJUnit.assertTrue(testIsConnected.start().equals("true"));
    }
}
