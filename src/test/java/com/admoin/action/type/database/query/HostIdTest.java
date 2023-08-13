package com.admoin.action.type.database.query;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class HostIdTest {
    private String resultTablePath = "test/table_name";
    private Select actionStringCompareUpsert = new Select(resultTablePath);
    private ConcurrentHashMap<Integer, Select> actionDataBaseQueryHostIdMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionDataBaseQueryHostIdMap.put(1, actionStringCompareUpsert);
    }

    @Test(groups = { "HostId" })
    public void newAction() {
        AssertJUnit.assertTrue(resultTablePath.equals(actionStringCompareUpsert.getResultTablePath()));
    }

    @Test(groups = { "HostId" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionDataBaseQueryHostIdMap.get(1).getResultTablePath().equals(resultTablePath));
    }

    @Test(groups = { "HostId" })
    public void getTableName() {
        AssertJUnit.assertTrue(actionStringCompareUpsert.getResultTableName().equals("table_name"));
    }
}
