package com.admoin.action;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.App;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;
import com.admoin.action.type.app.get.Field;
import com.admoin.action.type.app.get.Propertie;
import com.admoin.action.type.database.query.Select;
import com.admoin.action.type.database.query.Upsert;
import com.admoin.action.type.file.Exists;
import com.admoin.action.type.file.Remove;
import com.admoin.action.type.file.Start;
import com.admoin.action.type.file.download.AwsS3;
import com.admoin.action.type.file.download.PublicFile;
import com.admoin.action.type.json.get.Json;
import com.admoin.action.type.json.get.Value;
import com.admoin.action.type.net.test.Connection;
import com.admoin.action.type.string.compare.Contains;
import com.admoin.action.type.string.compare.Equals;
import com.admoin.action.type.string.compare.Less;
import com.admoin.action.type.string.compare.LessVersion;
import com.admoin.action.type.string.format.Hex;
import com.admoin.action.type.system.get.Property;
import com.admoin.action.type.system.get.Variable;
import com.admoin.action.type.url.get.Text;
import com.admoin.action.type.zip.Unzip;

import tech.ydb.table.result.ResultSetReader;

public class Action implements Serializable {
    private static final long serialVersionUID = 7L;

    public static ConcurrentMap<Integer, Action> map = new ConcurrentHashMap<>();

    public static void getFromDataBase() {
        Log.logger.info("Action.getFromYandexDataBase()");

        String query = "SELECT"
                + "`action_id`,"
                + "`action_type_id`,"
                + "`start_interval_seconds`"
                + "FROM `action/action`";

        Log.logger.info("yandexDataBaseReadOnly.get(query)");
        ResultSetReader result = Host.getDataBaseReadOnly().getQuery(query);

        Log.logger.info("new action");
        ConcurrentHashMap<Integer, Action> actionMap = new ConcurrentHashMap<>();

        do {
            int id = (int) result.getColumn("action_id").getUint64();
            int typeId = (int) result.getColumn("action_type_id").getUint64();
            int startIntervalSeconds = (int) result.getColumn("start_interval_seconds").getUint64();

            Action action = new Action(id, typeId, startIntervalSeconds);

            actionMap.put(id, action);

            Log.logger.info("New action. id: " + id + " containsText: " + typeId
                    + " startIntervalSeconds: " + startIntervalSeconds);
        } while (result.next());

        Action.map = actionMap;
    }

    private int id;
    private int typeId;
    private int startIntervalSeconds;

    private String result;
    private String resultOld;
    private LocalDateTime lastStart;

    public void setResultOld(String resultOld) {
        this.resultOld = resultOld;
    }
    private Boolean actionNotFound = false;
    private Boolean synchronizedWithDatabase = false;

    private boolean completed;

    public Boolean getSynchronizedWithDatabase() {
        return synchronizedWithDatabase;
    }

    public void setSynchronizedWithDatabase(Boolean synchronizedWithDatabase) {
        this.synchronizedWithDatabase = synchronizedWithDatabase;
    }

    public Boolean getActionNotFound() {
        return actionNotFound;
    }

    public void setActionNotFound(Boolean actionNotFound) {
        this.actionNotFound = actionNotFound;
    }
    public Action(int id, int typeId, int startIntervalSeconds) {
        this(id, typeId, startIntervalSeconds, "", LocalDateTime.now().minusSeconds(++startIntervalSeconds));
    }

    public Action(Action action) {
        this.id = action.id;
        this.typeId = action.typeId;
        this.startIntervalSeconds = action.startIntervalSeconds;
        this.result = action.result;
        this.lastStart = action.lastStart;
        this.synchronizedWithDatabase = action.synchronizedWithDatabase;
    }

    public Action(int id, int typeId, int startIntervalSeconds, String result, LocalDateTime lastStart) {
        this.id = id;
        this.typeId = typeId;
        this.startIntervalSeconds = startIntervalSeconds;
        this.result = result;
        this.lastStart = lastStart;
    }

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getStartIntervalSeconds() {
        return startIntervalSeconds;
    }

    public String getResult() {
        return result;
    }

    public LocalDateTime getLastStart() {
        return lastStart;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void start(String source, Host host) throws Exception {
        completed = false;
        Type type = Type.map.get(typeId);
        this.lastStart = LocalDateTime.now();

        switch (type.getName()) {// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
            case "app.get.Field":
                startAppGetField(host);
                break;

            case "app.get.Propertie":
                startAppGetPropertie();
                break;

            case "database.query.Upsert":
                startDataBaseQueryUpsert();
                break;

            case "database.query.Select":
                startDatabaseQuerySelect();
                break;

            case "file.Exists":
                startFileExists(source);
                break;

            case "file.Remove":
                startFileRemove(source);
                break;

            case "file.Start":
                startFileStart(source);
                break;

            case "file.download.AwsS3":
                startFileDownloadAwsS3(source);
                break;

            case "file.download.PublicFile":
                startFileDownloadPublicFile(source);
                break;

            case "json.get.Json":
                startJsonGetJson();
                break;

            case "json.get.Value":
                startJsonGetValue(source);
                break;

            case "net.test.Connection":
                startNetTestConnection();
                break;

            case "string.compare.Contains":
                startStringCompareContains(source);
                break;

            case "string.compare.Equals":
                startStringCompareEquals(source);
                break;

            case "string.compare.Less":
                startStringCompareLess(source);
                break;

            case "string.compare.LessVersion":
                startStringCompareLessVersion(source);
                break;

            case "string.format.Hex":
                startStringFormatHex(source);
                break;

            case "system.get.Property":
                startSystemGetProperty();
                break;

            case "system.get.Variable":
                startSystemGetVariable();
                break;

            case "url.get.Text":
                startUrlGetText(source);
                break;

            case "url.zip.Unzip":
                startZipUnzip(source);
                break;

            default:
                this.setActionNotFound(true);
                break;
        }
        completed = true;
    }

    public boolean isBroken() {
        if (getId() == 0) {
            App.setUpdateConfig(true);
        }
        return App.isUpdateConfig();
    }

    public boolean isTimeToStart() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        LocalDateTime lastStartPlusStartIntervalSeconds = lastStart.plusSeconds(startIntervalSeconds);
        Boolean isAfterLocalDateTimeNow = !localDateTimeNow.isBefore(lastStartPlusStartIntervalSeconds);
        Boolean isAfterHostTimeToStart = !lastStart.isAfter(Host.timeToStart);

        return isAfterLocalDateTimeNow && isAfterHostTimeToStart;
    }

    public boolean isCompleted() {
        return Boolean.logicalAnd(completed, !getActionNotFound());
    }

    private void startFileExists(String source) {
        if (Exists.map.containsKey(id)) {
            this.setResult(Exists.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startFileRemove(String source) {
        if (Remove.map.containsKey(id)) {
            this.setResult(Remove.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startFileStart(String source) {
        if (Start.map.containsKey(id)) {
            this.setResult(Start.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startZipUnzip(String source) {
        if (Unzip.map.containsKey(id)) {
            this.setResult(Unzip.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startDataBaseQueryUpsert() {
        if (Upsert.map.containsKey(id)) {
            this.setResult(Upsert.map.get(id).start());
            Action.map.get(Upsert.map.get(id).getActionIdResult()).setSynchronizedWithDatabase(Boolean.parseBoolean(this.getResult()));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startDatabaseQuerySelect() {
        if (Select.map.containsKey(id)) {
            this.setResult(Select.map.get(id).start());
            this.setSynchronizedWithDatabase(this.getResult().equals("false"));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startUrlGetText(String source) throws Exception {
        if (Text.map.containsKey(id)) {
            this.setResult(Text.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startSystemGetVariable() {
        if (Variable.map.containsKey(id)) {
            this.setResult(Variable.map.get(id).start());
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startSystemGetProperty() {
        if (Property.map.containsKey(id)) {
            this.setResult(Property.map.get(id).start());
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startStringFormatHex(String source) {
        if (Hex.map.containsKey(id)) {
            this.setResult(Hex.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startFileDownloadPublicFile(String source) {
        if (PublicFile.map.containsKey(id)) {
            this.setResult(PublicFile.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startJsonGetJson() throws MalformedURLException, URISyntaxException {
        if (Json.map.containsKey(id)) {
            this.setResult(Json.map.get(id).start());
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startJsonGetValue(String source) {
        if (Value.map.containsKey(id)) {
            this.setResult(Value.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startNetTestConnection() throws UnknownHostException {
        if (Connection.map.containsKey(id)) {
            this.setResult(Connection.map.get(id).start());
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startFileDownloadAwsS3(String source) throws IOException {
        if (AwsS3.map.containsKey(id)) {
            this.setResult(AwsS3.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startStringCompareLessVersion(String source) {
        if (LessVersion.map.containsKey(id)) {
            this.setResult(LessVersion.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startStringCompareLess(String source) {
        if (Less.map.containsKey(id)) {
            this.setResult(Less.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startStringCompareEquals(String source) {
        if (Equals.map.containsKey(id)) {
            this.setResult(Equals.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startStringCompareContains(String source) {
        if (Contains.map.containsKey(id)) {
            this.setResult(Contains.map.get(id).start(source));
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startAppGetPropertie() {
        if (Propertie.map.containsKey(id)) {
            this.setResult(Propertie.map.get(id).start());
        } else {
            this.setActionNotFound(true);
        }
    }

    private void startAppGetField(Host host) {
        if (Field.map.containsKey(id)) {
            this.setResult(Field.map.get(id).start(host));
        } else {
            this.setActionNotFound(true);
        }
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getResultOld() {
        return resultOld;
    }
}
