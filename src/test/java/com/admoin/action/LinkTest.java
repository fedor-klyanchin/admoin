package com.admoin.action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.admoin.Host;
import com.admoin.Log;

public class LinkTest {
    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();
    }

    @Test(groups = { "Link" })
    public void newAction() {
        Boolean result = false;

        ConcurrentMap<Integer, List<Link>> linkMap = new ConcurrentHashMap<>();
        Link link = new Link(1, 2, false);
        List<Link> linkList = new ArrayList<>();
        linkList.add(link);
        linkMap.put(1, linkList);

        for (Link linkItem : linkList) {
            if (linkItem.getFromId() == 1 && linkItem.getToId() == 2 && Boolean.FALSE.equals(linkItem.getFromFalseResult())) {
                result = true;
            }
        }

        AssertJUnit.assertTrue(result);
    }
}
