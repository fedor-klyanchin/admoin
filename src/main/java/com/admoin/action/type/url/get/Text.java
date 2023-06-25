package com.admoin.action.type.url.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Text implements Serializable {
    private static final long serialVersionUID = 10L;

    private static String tablePath = "action/type/url/get/text";
    private static String tableName = Type.getTableName(tablePath);

    public String urlString;
    public String containsText;
    public String delimiterFirst;
    public String delimiterSecond;
    public Boolean useSecondDelimiterFirst;

    public Text(
            String urlString,
            String containsText,
            String delimiterFirst,
            String delimiterSecond,
            Boolean useSecondDelimiterFirst) {
        this.urlString = urlString;
        this.containsText = containsText;
        this.delimiterFirst = delimiterFirst;
        this.delimiterSecond = delimiterSecond;
        this.useSecondDelimiterFirst = useSecondDelimiterFirst;
    }

    public static ConcurrentMap<Integer, Text> getFromDataBase() {
        Log.logger.info("ActionFileText.getFromYandexDataBase()");

        Log.logger.info("new ActionFileText[]");
        ConcurrentHashMap<Integer, Text> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.dataBaseReadOnly.getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String urlString = DataBase.getColumnString(result, "?_url".replace("?", tableName));
            String containsText = DataBase.getColumnString(result, "?_contains_text".replace("?", tableName));
            String delimiterFirst = DataBase.getColumnString(result, "?_delimiter_first".replace("?", tableName));
            String delimiterSecond = DataBase.getColumnString(result, "?_delimiter_second".replace("?", tableName));
            Boolean useSecondDelimiterFirst = DataBase.getColumnBoolean(result, "?_use_second_delimiter_first".replace("?", tableName));
            
            Text action = new Text(urlString,
                    containsText,
                    delimiterFirst,
                    delimiterSecond,
                    useSecondDelimiterFirst);

            actionMap.put(actionId, action);

            Log.logger
                    .info("New action. actionId: " + actionId + " containsText: " + containsText);
        } while (result.next());

        return actionMap;
    }

    public String getUrlString() {
        return urlString;
    }

    public String getContainsText() {
        return containsText;
    }

    public String getDelimiterFirst() {
        return delimiterFirst;
    }

    public String getDelimiterSecond() {
        return delimiterSecond;
    }

    public Boolean getUseSecondDelimiterFirst() {
        return useSecondDelimiterFirst;
    }

    private String getTextFromContent(List<String> content) {
        String report = "";

        for (String string : content) {
            if (containsText.length() == 0 ||
                    string.contains(containsText)) {
                report = string;
            }
        }

        if (delimiterFirst != null || delimiterSecond != null) {
            if (!useSecondDelimiterFirst) {
                int lastIndexOf = report.lastIndexOf(delimiterFirst);
                report = report.substring(lastIndexOf + 1, report.length());
                report = report.substring(0, report.indexOf(delimiterSecond));
            } else {
                report = report.substring(0, report.indexOf(delimiterSecond));
                int lastIndexOf = report.lastIndexOf(delimiterFirst);
                report = report.substring(lastIndexOf + 1, report.length());
            }
        }

        return report;
    }

    private List<String> getContentFromUrl(String urlString) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
            String inputLine;
            List<String> content = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                content.add(inputLine);
            }

            return content;
        }
    }

    public String start() throws Exception {
        String source = "";
        return this.start(source);
    }

    public String start(String source) throws Exception {
        List<String> content = this.getContentFromUrl(urlString.replace("%s", source));
        return this.getTextFromContent(content);
    }
}