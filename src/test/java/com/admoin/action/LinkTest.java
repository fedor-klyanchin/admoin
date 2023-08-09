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
    private List<Link> linkList0 = new ArrayList<>();
    private List<Link> linkList1 = new ArrayList<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        Log.create();
        Host.getProperties();

        Link link0 = new Link(0, 1, false);
        Link link1 = new Link(1, 2, false);

        linkList0.add(link0);
        linkList1.add(link1);

        linkMap.put(0, linkList0);
        linkMap.put(1, linkList1);
    }

    @Test(groups = { "Link" })
    public void newAction() {
        Boolean result = false;

        for (Link linkItem : linkList1) {
            if (linkItem.getFromId() == 1 && linkItem.getToId() == 2 && Boolean.FALSE.equals(linkItem.getFromFalseResult())) {
                result = true;
            }
        }

        AssertJUnit.assertTrue(result);
    }

    @Test(groups = { "Link" })
    public void linkMapWithoutIncorrectElement() {
        int listLinkSize = linkMap.get(0).size();
        AssertJUnit.assertTrue(listLinkSize == 1);
    }
}
