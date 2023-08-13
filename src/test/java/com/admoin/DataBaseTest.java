package com.admoin;

import org.testng.annotations.*;

import com.admoin.action.Action;
import com.admoin.action.Link;
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
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import tech.ydb.table.result.ResultSetReader;

import java.io.FileReader;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.testng.AssertJUnit;

public class DataBaseTest {
    private Host host;
    private String testValue = "TEST";

    ConcurrentMap<Integer, Action> actionMap = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, List<Link>> linkMap = new ConcurrentHashMap<>();
    ConcurrentMap<Integer, Type> typeMap = new ConcurrentHashMap<>();

    @Test
    @BeforeClass
    public void setUp() throws Exception {
        // code that will be invoked when this test is instantiated
        Log.create();
        Host.getProperties();
        host = new Host();
        Host.restoreFromLocalFile();

        DataBase.closeAll();

        Gson gson = new Gson();
        Map<String, String> configDataBaseTest = new HashMap<>();

        JsonReader reader = new JsonReader(new FileReader("config-database-test.json"));
        configDataBaseTest = gson.fromJson(reader, configDataBaseTest.getClass());

        Host.getDataBaseReadOnly()
                .setConnectionString(configDataBaseTest.get("yandex_data_base_read_only_connection_string"));
        Host.getDataBaseReadOnly().setSaKeyFile(configDataBaseTest.get("yandex_data_base_read_only_sa_key_file"));

        Host.getDataBaseReadWrite().close();
        Host.getDataBaseReadWrite()
                .setConnectionString(configDataBaseTest.get("yandex_data_base_read_write_connection_string"));
        Host.getDataBaseReadWrite().setSaKeyFile(configDataBaseTest.get("yandex_data_base_read_write_sa_key_file"));

        DataBase.openAll();
    }

    @Test(groups = { "DataBase" })
    public void dataBaseNewReadOnly() {
        String yandexDataBaseReadOnlyConnectionString = Host.properties
                .getProperty("yandex_data_base_read_only_connection_string");
        String yandexDataBaseReadOnlySaKeyFile = Host.properties.getProperty("yandex_data_base_read_only_sa_key_file");
        DataBase yandexDataBaseReadOnly = new DataBase(yandexDataBaseReadOnlyConnectionString,
                yandexDataBaseReadOnlySaKeyFile);
        AssertJUnit.assertTrue(
                yandexDataBaseReadOnly.getConnectionString().equals(yandexDataBaseReadOnlyConnectionString) &&
                        yandexDataBaseReadOnly.getSaKeyFile().equals(yandexDataBaseReadOnlySaKeyFile));
    }

    @Test(groups = { "DataBase" })
    public void dataBaseNewReadWrite() {
        String yandexDataBaseReadWriteConnectionString = Host.properties
                .getProperty("yandex_data_base_read_write_connection_string");
        String yandexDataBaseReadWriteSaKeyFile = Host.properties
                .getProperty("yandex_data_base_read_write_sa_key_file");
        DataBase yandexDataBaseReadWrite = new DataBase(yandexDataBaseReadWriteConnectionString,
                yandexDataBaseReadWriteSaKeyFile);
        AssertJUnit.assertTrue(
                yandexDataBaseReadWrite.getConnectionString().equals(yandexDataBaseReadWriteConnectionString) &&
                        yandexDataBaseReadWrite.getSaKeyFile().equals(yandexDataBaseReadWriteSaKeyFile));
    }

    @Test(groups = { "DataBase" })
    public void dataBaseIsConnected() throws UnknownHostException {
        AssertJUnit.assertTrue(DataBase.isConnected());
    }

    @Test(groups = { "DataBaseTest" })
    public void configGetFromDataBase() throws Exception {
        Map<String, String> config = new HashMap<>();
        config = Config.getFromDataBase();
        String appVersionfromConfig = config.get(App.getAppVersionPropertyName());
        AssertJUnit.assertTrue(!appVersionfromConfig.equals(null));
    }

    @Test(groups = { "DataBaseTest" })
    public void testGetActionFromDataBase() {
        Action.getFromDataBase();

        AssertJUnit.assertTrue(Action.map.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testGetActionFromDataBase" })
    public void getActionFromDataBaseCorrectValue() {
        Action.getFromDataBase();

        Action action = Action.map.get(1);

        AssertJUnit.assertTrue(
                action.getId() == 1 && action.getTypeId() == 1 && action.getStartIntervalSeconds() == 0);
    }

    @Test(groups = { "DataBaseTest" })
    public void testLinkGetFromDataBase() {
        Link.getFromDataBase();
        AssertJUnit.assertTrue(Link.map.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testLinkGetFromDataBase" })
    public void getLinkFromDataBaseCorrectValue() {
        Boolean result = false;

        Link.getFromDataBase();

        List<Link> linkList = new ArrayList<>();
        linkList = Link.map.get(2);

        for (Link linkItem : linkList) {
            if (linkItem.getFromId() == 2 && linkItem.getToId() == 4
                    && Boolean.FALSE.equals(linkItem.getFromFalseResult())) {
                result = true;
            }
        }

        AssertJUnit.assertTrue(result);
    }

    @Test(groups = { "DataBaseTest" })
    public void testGetTypeFromDataBase() {
        Type.getFromDataBase();

        AssertJUnit.assertTrue(Type.map.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testGetTypeFromDataBase" })
    public void getTypeFromDataBaseCorrectValue() {
        Type.getFromDataBase();

        Type type = Type.map.get(1);

        AssertJUnit.assertTrue(type.getId() == 1 && type.getName().equals("file.Exists"));
    }

    @Test(groups = { "DataBaseTest" })
    public void hostGetNewIdFromDataBase() throws Exception {
        int newIdFromDataBase = host.getNewIdFromDataBase();
        AssertJUnit.assertTrue(newIdFromDataBase != 0);
    }

    @Test(groups = { "DataBaseTest" })
    public void hostSetId() throws Exception {
        host.setId(0);
        Host.getProperties();
        AssertJUnit.assertTrue(host.id == 1);
        Host.restoreFromLocalFile();
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "hostSetId" })
    public void hostIsReady() throws Exception {
        AssertJUnit.assertTrue(host.isReady());
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "hostGetDataFromDataBase" })
    public void hostWriteCurrentDateTimeToDataBaseTable() throws Exception {
        LocalDateTime startlocalDateTime = LocalDateTime.now(ZoneOffset.UTC);
        App.sleep(1);
        Host.writeCurrentDateTimeToDataBaseTable("online_datetime");

        String query = "SELECT `" + "online_datetime" + "_value` "
                + "FROM `" + "online_datetime" + "` "
                + "WHERE `" + "online_datetime" + "_host_id` " + " == " + Host.properties.getProperty("id") + ";";

        ResultSetReader resultQuery = Host.getDataBaseReadWrite().getQuery(query);

        LocalDateTime localDateTimeNowFromDataBase;
        do {
            localDateTimeNowFromDataBase = resultQuery.getColumn("online_datetime_value").getDatetime();
        } while (resultQuery.next());

        Boolean result = false;
        if (!localDateTimeNowFromDataBase.isBefore(startlocalDateTime) &&
                localDateTimeNowFromDataBase.isBefore(startlocalDateTime.plusMinutes(5))) {
            result = true;
        }

        AssertJUnit.assertTrue(result);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "hostGetDataFromDataBase" })
    public void appIsGetDataFromDataBase() throws Exception {
        Boolean oldVersionApp = App.isOldVersionApp();
        Boolean oldVersionConfig = App.isOldVersionConfig();
        Boolean updateConfig = App.isUpdateConfig();
        Boolean appIsGetDataFromDataBase = App.isGetDataFromDataBase();
        Boolean appIsDatabaseConnectionStringChanged = App.isDatabaseConnectionStringChanged();

        AssertJUnit.assertTrue(
                (oldVersionApp ||
                        oldVersionConfig ||
                        updateConfig || appIsDatabaseConnectionStringChanged) == appIsGetDataFromDataBase);
    }

    @Test(groups = { "DataBaseTest" })
    public void hostGetDataFromDataBase() throws Exception {
        host.getDataFromDataBase();

        AssertJUnit.assertTrue(
                Action.map.size() != 0 &&
                        Type.map.size() != 0 &&
                        Link.map.size() != 0 &&
                        Field.map.size() != 0 &&
                        Propertie.map.size() != 0 &&
                        Upsert.map.size() != 0 &&
                        Select.map.size() != 0 &&
                        AwsS3.map.size() != 0 &&
                        PublicFile.map.size() != 0 &&
                        Exists.map.size() != 0 &&
                        Remove.map.size() != 0 &&
                        Start.map.size() != 0 &&
                        Json.map.size() != 0 &&
                        Value.map.size() != 0 &&
                        Connection.map.size() != 0 &&
                        Contains.map.size() != 0 &&
                        Equals.map.size() != 0 &&
                        Less.map.size() != 0 &&
                        LessVersion.map.size() != 0 &&
                        Hex.map.size() != 0 &&
                        Property.map.size() != 0 &&
                        Variable.map.size() != 0 &&
                        Text.map.size() != 0 &&
                        Unzip.map.size() != 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "hostGetDataFromDataBase" })
    void testStartActionDataBaseSelectTableHost() {
        String tablePath = "host";
        Select actionSelectHost = new Select(tablePath);

        String result = actionSelectHost.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" })
    void testStartActionDataBaseSelectTableOther() {
        String tablePath = "test/test_test";
        Select actionSelect = new Select(tablePath);

        String result = actionSelect.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" })
    void testStartActionDataBaseUpsertTableHost() {
        String tablePath = "host";
        Upsert actionUpsert = new Upsert(10, tablePath);

        Action action = new Action(10, 1, 0);
        action.setResult(testValue);
        Action.map.put(10, action);

        actionUpsert.start();

        Select actionSelect = new Select(tablePath);
        String result = actionSelect.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" })
    void testStartActionDataBaseUpsertTableOther() {
        String tablePath = "test/test_test";
        Upsert actionUpsert = new Upsert(10, tablePath);

        Action action = new Action(10, 1, 0);
        action.setResult(testValue);
        Action.map.put(10, action);

        actionUpsert.start();

        Select actionSelect = new Select(tablePath);
        String result = actionSelect.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" }, dependsOnMethods = { "hostGetDataFromDataBase" })
    public void startActionMapOneActionTrue() throws Exception {
        host.startActionMap();

        AssertJUnit.assertTrue(Action.map.get(7).getResult().equals("true"));
    }
}
