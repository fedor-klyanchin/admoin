package com.admoin.action;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.Host;
import com.admoin.Log;

import tech.ydb.table.result.ResultSetReader;

public class Link implements Serializable {
    private static final long serialVersionUID = 8L;

    public static ConcurrentMap<Integer, List<Link>> map = new ConcurrentHashMap<>();

    private int fromId;
    private int toId;
    private Boolean fromFalseResult;
    private String source;

    public Link(int fromId, int toId, Boolean fromFalseResult) {
        this(fromId, toId, fromFalseResult, "");
    }

    public Link(int fromId, int toId, Boolean fromFalseResult, String source) {
        this.fromId = fromId;
        this.toId = toId;
        this.fromFalseResult = fromFalseResult;
        this.source = source;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getFromFalseResult() {
        return fromFalseResult;
    }

    public Action getActionFromMap() {
        return Action.map.getOrDefault(getToId(), new Action(0, 0, 0));
    }

    public String getActionResult() {
        return getActionFromMap().getResult();
    }

    public boolean isActionResultEqualsFalse() {
        return Boolean.logicalOr(getActionResult().equals("false"), getActionResult().equals("0"));
    }

    public static void getFromDataBase() {
        Log.logger.info("Link.getFromYandexDataBase()");

        String query = "SELECT"
                + "`link_from_id`,"
                + "`link_to_id`,"
                + "`link_from_false_result`"
                + "FROM `action/link`";

        Log.logger.info("yandexDataBaseReadOnly.get(query)");
        ResultSetReader result = Host.dataBaseReadOnly.getQuery(query);

        Log.logger.info("new Link[]");
        ConcurrentHashMap<Integer, List<Link>> linkMap = new ConcurrentHashMap<>();

        do {
            int fromId = (int) result.getColumn("link_from_id").getUint64();
            int toId = (int) result.getColumn("link_to_id").getUint64();
            Boolean fromFalseResult = result.getColumn("link_from_false_result").getBool();

            Link link = new Link(fromId, toId, fromFalseResult);

            List<Link> linkList = new ArrayList<>();
            if (linkMap.contains(fromId)) {
                linkList = linkMap.get(fromId);
            }
            linkList.add(link);
            linkMap.put(fromId, linkList);

            Log.logger.info("New Link. fromId: " + fromId + " toId: " + toId);
        } while (result.next());

        Link.map = linkMap;
    }
}
