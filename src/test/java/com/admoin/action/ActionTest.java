package com.admoin.action;

import java.util.HashMap;
import java.util.Map;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class ActionTest {
    @Test
    @BeforeClass
    public void beforeClass() throws Exception {
        Log.create();
        Host.getProperties();
        Host host = new Host();
        host.restoreFromLocalFile();
    }

    @Test(groups = { "Action" })
    public void newAction() {
        Map<Integer, Action> actionMap = new HashMap<>();
        Action action = new Action(1, 1, 0);
        actionMap.put(1, action);

        AssertJUnit.assertTrue(actionMap.get(1) == action && action.getId() == 1 && action.getTypeId() == 1
                && action.getStartIntervalSeconds() == 0);
    }
}
