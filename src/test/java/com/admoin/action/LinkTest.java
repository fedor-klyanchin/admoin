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
    private ConcurrentMap<Integer, List<Link>> linkMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();

        linkMap.put(0, Link.getLinkList(linkMap, new Link(0, 1, false)));
        linkMap.put(0, Link.getLinkList(linkMap, new Link(0, 1, false)));
        linkMap.put(1, Link.getLinkList(linkMap, new Link(1, 2, false)));
        linkMap.put(1, Link.getLinkList(linkMap, new Link(1, 3, false)));
        linkMap.put(2, Link.getLinkList(linkMap, new Link(2, 4, false)));
        linkMap.put(2, Link.getLinkList(linkMap, new Link(2, 5, false)));
}

    @Test(groups = { "Link" })
    public void newLink() {
        Boolean result = false;

        for (Link linkItem : linkMap.get(2)) {
            if (linkItem.getFromId() == 2 && linkItem.getToId() == 4 && Boolean.FALSE.equals(linkItem.getFromFalseResult())) {
                result = true;
            }
        }

        AssertJUnit.assertTrue(result);
    }

    @Test(groups = { "Link" })
    public void linkMapWithoutIncorrectElement() {
        AssertJUnit.assertTrue(
            linkMap.get(0).size() == 2 &&
            linkMap.get(1).size() == 2 &&
            linkMap.get(2).size() == 2 &&
            linkMap.get(3) == null &&
            linkMap.get(4) == null &&
            linkMap.get(5) == null
            );
    }
}
