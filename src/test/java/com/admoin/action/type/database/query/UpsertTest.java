package com.admoin.action.type.database.query;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.Action;

public class UpsertTest {
    private int actionIdResult = 2;
    private String resultTablePath = "test/table_name";
    private Upsert actionUpsert = new Upsert(actionIdResult, resultTablePath);
    private ConcurrentHashMap<Integer, Upsert> actionDataBaseQueryUpsertMap = new ConcurrentHashMap<>();
    private Action action = new Action(1, 1, 0);
    private Action actionResult = new Action(2, 1, 0);
    private ConcurrentMap<Integer, Action> actionMap = new ConcurrentHashMap<>();


    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionDataBaseQueryUpsertMap.put(1, actionUpsert);
    }

    @Test(groups = { "Upsert" })
    public void newAction() {
        AssertJUnit.assertTrue(resultTablePath.equals(actionUpsert.getResultTablePath()));
    }

    @Test(groups = { "Upsert" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionDataBaseQueryUpsertMap.get(1).getResultTablePath().equals(resultTablePath));
    }

    @Test(groups = { "Upsert" })
    public void getTableName() {
        AssertJUnit.assertTrue(actionUpsert.getResultTableName().equals("table_name"));
    }

    @Test(groups = { "Upsert" })
    public void queryContainsTableName() {
        AssertJUnit.assertTrue(actionUpsert
            .getQuery()
            .contains("table_name"));
    }

    @Test(groups = { "Upsert" })
    public void queryContainsCorrectResult() {
        actionResult.setResult("actionResult");
        actionMap.put(1, action);
        actionMap.put(2, actionResult);

        Action actionResultTest = actionMap.get(actionIdResult);
        actionUpsert.setResultTargetAction(actionResultTest.getResult());

        AssertJUnit.assertTrue(actionUpsert
            .getResultTargetAction()
            .equals("actionResult"));
    }
}
