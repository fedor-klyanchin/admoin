package com.admoin.action.type.system.get;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class VariableTest {
    private String variable = "PATH";
    private Variable actionJsonGetVariable = new Variable(variable);
    private ConcurrentHashMap<Integer, Variable> actionAppGetVariableMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetVariableMap.put(1, actionJsonGetVariable);
    }

    @Test(groups = { "Variable" })
    public void newAction() {
        AssertJUnit.assertTrue(variable.equals(actionJsonGetVariable.getName()));
    }

    @Test(groups = { "Variable" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetVariableMap.get(1).getName().equals(variable));
    }

    @Test(groups = { "Variable" })
    public void getResultTrue() throws IOException {
        Variable testGetJsonFromUrl = new Variable(variable);
        String result = testGetJsonFromUrl.start();
        AssertJUnit.assertTrue(result.equals(System.getenv(variable)));
    }
}
