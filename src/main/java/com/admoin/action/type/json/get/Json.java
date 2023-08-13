package com.admoin.action.type.json.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Json implements Serializable {
    private static final long serialVersionUID = 17L;

    private static String tablePath = "action/type/json/get/json_get_json";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Json> map = new ConcurrentHashMap<>();

    private String urlString;

    public Json(
            String urlString) {
        this.urlString = urlString;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileJson.getFromYandexDataBase()");

        Log.logger.info("new ActionFileJson[]");
        ConcurrentHashMap<Integer, Json> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String urlString = DataBase.getColumnString(result, "?_url".replace("?", tableName));

            Json action = new Json(urlString);

            actionMap.put(actionId, action);

            Log.logger.info("New actionAppGetJson [actionId=" + actionId + ", url=" + urlString + "]");
        } while (result.next());

        Json.map = actionMap;
    }

    public String getUrlString() {
        return urlString;
    }

    public String start() throws MalformedURLException, URISyntaxException {
        Log.logger.info("GetJsonFromUrl.start(" + urlString + ")");

        String jsonString = "";
        StringBuilder jsonStringBuilder = new StringBuilder();

        URI uri = new URI(urlString);
        URL url = uri.toURL();
        String inputLine;
        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
        ) {
            while ((inputLine = in.readLine()) != null) {
                jsonStringBuilder.append(inputLine);
            }
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        jsonString = jsonStringBuilder.toString();
        
        return jsonString;
    }
}