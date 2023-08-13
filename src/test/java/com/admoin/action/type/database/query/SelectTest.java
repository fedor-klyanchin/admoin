package com.admoin.action.type.database.query;

import java.util.concurrent.ConcurrentHashMap;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

public class SelectTest {
    private String tablePath = "host";
    private String tableName = Type.getTableName(tablePath);
    private Select actionSelect = new Select(tablePath);
    private ConcurrentHashMap<Integer, Select> actionDataBaseQuerySelectMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionDataBaseQuerySelectMap.put(1, actionSelect);
    }

    @Test(groups = { "Select" })
    public void newAction() {
        AssertJUnit.assertTrue(tablePath.equals(actionSelect.getResultTablePath()));
    }

    @Test(groups = { "Select" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionDataBaseQuerySelectMap.get(1).getResultTablePath().equals(tablePath));
    }

    @Test(groups = { "Select" })
    public void getTableName() {
        AssertJUnit.assertTrue(actionSelect.getResultTableName().equals("host"));
    }

    @Test
    void testGetFromDataBase() {

    }

    @Test
    void testGetResultTableName() {
        AssertJUnit.assertTrue(actionSelect
            .getResultTableName()
            .equals(tableName));
    }

    @Test
    void testGetResultTablePath() {
        AssertJUnit.assertTrue(actionSelect
            .getResultTablePath()
            .equals(tablePath));
    }
}
