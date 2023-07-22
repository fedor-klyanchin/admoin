package com.admoin.action.type.database.query;

import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.Action;

public class UpsertTest {
    private String resultTablePath = "test/table_name";
    private Upsert actionStringCompareUpsert = new Upsert(resultTablePath);
    private ConcurrentHashMap<Integer, Upsert> actionDataBaseQueryUpsertMap = new ConcurrentHashMap<>();
    private Action action = new Action(1, 1, 0);

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionDataBaseQueryUpsertMap.put(1, actionStringCompareUpsert);
    }

    @Test(groups = { "Upsert" })
    public void newAction() {
        AssertJUnit.assertTrue(resultTablePath.equals(actionStringCompareUpsert.getResultTablePath()));
    }

    @Test(groups = { "Upsert" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionDataBaseQueryUpsertMap.get(1).getResultTablePath().equals(resultTablePath));
    }

    @Test(groups = { "Upsert" })
    public void getTableName() {
        AssertJUnit.assertTrue(actionStringCompareUpsert.getResultTableName().equals("table_name"));
    }

    @Test(groups = { "Upsert" })
    public void getQuery() {
        AssertJUnit.assertTrue(actionStringCompareUpsert
            .getQuery(action)
            .contains("table_name"));
    }
}
