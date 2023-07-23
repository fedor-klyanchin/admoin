package com.admoin.action.type;

import java.util.HashMap;
import java.util.Map;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class TypeTest {
    @Test
    @BeforeGroups("DataBase")
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
    }

    @Test(groups = { "Type" })
    public void newType() {
        Map<Integer, Type> typeMap = new HashMap<>();
        Type type = new Type(1, "file.Exists", null);
        typeMap.put(1, type);

        AssertJUnit.assertTrue(typeMap.get(1) == type && type.getId() == 1 && type.getName() == "file.Exists" && type.getTablePath() == "action/type/type");
    }
}
