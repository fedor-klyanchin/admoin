package com.admoin.action.type.string.format;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class HexTest {
    private String string = "-host:";
    private Hex action = new Hex(string);
    private ConcurrentHashMap<Integer, Hex> actionAppGetHexMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
        actionAppGetHexMap.put(1, action);
    }

    @Test(groups = { "Hex" })
    public void newAction() {
        AssertJUnit.assertTrue(string.equals(action.getString()));
    }

    @Test(groups = { "Hex" })
    public void newActionMap() {
        AssertJUnit.assertTrue(actionAppGetHexMap.get(1).getString().equals(string));
    }

    @Test(groups = { "Hex" })
    public void getResultTrue() throws IOException {
        String stringHex = action.start(string);
        AssertJUnit.assertTrue(stringHex.equals("2d686f73743a"));
    }
}
