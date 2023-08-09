package com.admoin;

import org.testng.annotations.*;

import com.admoin.action.Action;
import com.admoin.action.Link;
import com.admoin.action.type.Type;
import com.admoin.action.type.database.query.Select;
import com.admoin.action.type.database.query.Upsert;
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
        host.restoreFromLocalFile();

        DataBase.closeAll();

        Gson gson = new Gson();
        Map<String, String> configDataBaseTest = new HashMap<>();     

        JsonReader reader = new JsonReader(new FileReader("config-database-test.json"));
        configDataBaseTest = gson.fromJson(reader, configDataBaseTest.getClass());

        Host.dataBaseReadOnly.setConnectionString(configDataBaseTest.get("yandex_data_base_read_only_connection_string"));
        Host.dataBaseReadOnly.setSaKeyFile(configDataBaseTest.get("yandex_data_base_read_only_sa_key_file"));

        Host.dataBaseReadWrite.close();
        Host.dataBaseReadWrite.setConnectionString(configDataBaseTest.get("yandex_data_base_read_write_connection_string"));
        Host.dataBaseReadWrite.setSaKeyFile(configDataBaseTest.get("yandex_data_base_read_write_sa_key_file"));

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
        actionMap = Action.getFromDataBase();

        AssertJUnit.assertTrue(actionMap.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testGetActionFromDataBase" })
    public void getActionFromDataBaseCorrectValue() {
        actionMap = Action.getFromDataBase();

        Action action = actionMap.get(1);

        AssertJUnit.assertTrue(
                action.getId() == 1 && action.getTypeId() == 1 && action.getStartIntervalSeconds() == 0);
    }

    @Test(groups = { "DataBaseTest" })
    public void testLinkGetFromDataBase() {
        linkMap = Link.getFromDataBase();
        AssertJUnit.assertTrue(linkMap.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testLinkGetFromDataBase" })
    public void getLinkFromDataBaseCorrectValue() {
        Boolean result = false;

        linkMap = Link.getFromDataBase();

        List<Link> linkList = new ArrayList<>();
        linkList = linkMap.get(2);

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
        typeMap = Type.getFromDataBase();

        AssertJUnit.assertTrue(typeMap.size() > 0);
    }

    @Test(groups = { "DataBaseTest" }, dependsOnMethods = { "testGetTypeFromDataBase" })
    public void getTypeFromDataBaseCorrectValue() {
        typeMap = Type.getFromDataBase();

        Type type = typeMap.get(1);

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
        host.restoreFromLocalFile();
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

        ResultSetReader resultQuery = Host.dataBaseReadWrite.getQuery(query);

        LocalDateTime localDateTimeNowFromDataBase;
        do {
            localDateTimeNowFromDataBase = resultQuery.getColumn("online_datetime_value").getDatetime();
        } while (resultQuery.next());

        Boolean result = false;
        if (!localDateTimeNowFromDataBase.isBefore(startlocalDateTime) &&
                localDateTimeNowFromDataBase.isBefore(startlocalDateTime.plusMinutes(1))) {
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

        AssertJUnit.assertTrue(
                (oldVersionApp ||
                        oldVersionConfig ||
                        updateConfig) == appIsGetDataFromDataBase);
    }

    @Test(groups = { "DataBaseTest" })
    public void hostGetDataFromDataBase() throws Exception {
        Host.getDataFromDataBase();

        AssertJUnit.assertTrue(
                Host.actionMap.size() != 0 &&
                        Host.actionTypeMap.size() != 0 &&
                        Host.actionLinkMap.size() != 0 &&
                        Host.actionTypeAppGetField.size() != 0 &&
                        Host.actionTypeAppGetPropertie.size() != 0 &&
                        Host.actionTypeDatabaseQueryUpsert.size() != 0 &&
                        Host.actionTypeDatabaseQuerySelect.size() != 0 &&
                        Host.actionTypeFileDownloadAwsS3.size() != 0 &&
                        Host.actionTypeFileDownloadPublicFile.size() != 0 &&
                        Host.actionTypeFileExists.size() != 0 &&
                        Host.actionTypeFileRemove.size() != 0 &&
                        Host.actionTypeFileStart.size() != 0 &&
                        Host.actionTypeJsonGetJson.size() != 0 &&
                        Host.actionTypeJsonGetValue.size() != 0 &&
                        Host.actionTypeNetTestConnection.size() != 0 &&
                        Host.actionTypeStringCompareContains.size() != 0 &&
                        Host.actionTypeStringCompareEquals.size() != 0 &&
                        Host.actionTypeStringCompareLess.size() != 0 &&
                        Host.actionTypeStringCompareLessVersion.size() != 0 &&
                        Host.actionTypeStringFormatHex.size() != 0 &&
                        Host.actionTypeSystemgetProperty.size() != 0 &&
                        Host.actionTypeSystemgetVariable.size() != 0 &&
                        Host.actionTypeUrlGetText.size() != 0 &&
                        Host.actionTypeZipUnzip.size() != 0);
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
        Upsert actionUpsert = new Upsert(tablePath);
        Action action = new Action(1, 1, 0);
        action.setResult(testValue);
        actionUpsert.start(action);

        Select actionSelect = new Select(tablePath);
        String result = actionSelect.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" })
    void testStartActionDataBaseUpsertTableOther() {
        String tablePath = "test/test_test";
        Upsert actionUpsert = new Upsert(tablePath);
        Action action = new Action(1, 1, 0);
        action.setResult(testValue);
        actionUpsert.start(action);

        Select actionSelect = new Select(tablePath);
        String result = actionSelect.start();
        AssertJUnit.assertTrue(result.equals(testValue));
    }

    @Test(groups = { "DataBase" }, dependsOnMethods = { "hostGetDataFromDataBase" })
    public void startActionMapOneActionTrue() throws Exception {
        host.startActionMap();

        AssertJUnit.assertTrue(Host.actionMap.get(7).getResult().equals("true"));
    }
}
