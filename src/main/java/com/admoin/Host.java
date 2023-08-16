package com.admoin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.action.Action;
import com.admoin.action.Link;
import com.admoin.action.type.Type;
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

public class Host implements Serializable {
    private static final long serialVersionUID = 2L;
    public String name;
    public int id;

    public String getName() {
        return name;
    }

    public ConcurrentMap<Integer, Type> getActionTypeMap() {
        return actionTypeMap;
    }

    public void setActionTypeMap(ConcurrentMap<Integer, Type> actionTypeMap) {
        this.actionTypeMap = actionTypeMap;
    }

    public ConcurrentMap<Integer, List<Link>> getActionLinkMap() {
        return actionLinkMap;
    }

    public void setActionLinkMap(ConcurrentMap<Integer, List<Link>> actionLinkMap) {
        this.actionLinkMap = actionLinkMap;
    }

    public ConcurrentMap<Integer, Action> getActionMap() {
        return actionMap;
    }

    public void setActionMap(ConcurrentMap<Integer, Action> actionMap) {
        this.actionMap = actionMap;
    }

    public String appVersion = App.APP_VERSION;
    public String configVersion = Host.properties.getProperty(App.getConfigVersionPropertyName());
    public static Properties properties;

    private static DataBase dataBaseReadOnly;
    private static DataBase dataBaseReadWrite;
    
    public static DataBase getDataBaseReadOnly() {
        return dataBaseReadOnly;
    }

    public static void setDataBaseReadOnly() {
        String yandexDataBaseReadOnlyConnectionString = Host.properties
                .getProperty(namePropertyYandexDataBaseReadOnlyConnectionString);
        String yandexDataBaseReadOnlySaKeyFile = Host.properties.getProperty("yandex_data_base_read_only_sa_key_file");

        dataBaseReadOnly = new DataBase(yandexDataBaseReadOnlyConnectionString, yandexDataBaseReadOnlySaKeyFile);
    }

    public static DataBase getDataBaseReadWrite() {
        return dataBaseReadWrite;
    }

    public static void setDataBaseReadWrite() {
        String yandexDataBaseReadWriteConnectionString = Host.properties
                .getProperty(namePropertyYandexDataBaseReadWriteConnectionString);
        String yandexDataBaseReadWriteSaKeyFile = Host.properties
                .getProperty("yandex_data_base_read_write_sa_key_file");
        dataBaseReadWrite = new DataBase(yandexDataBaseReadWriteConnectionString,
                yandexDataBaseReadWriteSaKeyFile);
    }

    // https://docs.oracle.com/javase/tutorial/essential/environment/properties.html
    private static Properties systemPropertyes = System.getProperties();
    private static String userDir = systemPropertyes.getProperty("user.dir");
    private static String fileSeparator = systemPropertyes.getProperty("file.separator");
    private static String pathLocalFiles = userDir + fileSeparator;

    static String pathPropertiesDefault = pathLocalFiles + "PropertiesDefault.properties";
    static String pathPropertiesCurrent = pathLocalFiles + "PropertiesCurrent.properties";
    static String pathPropertiesId = pathLocalFiles + "PropertiesId.properties";
    static String pathPropertiesDataBase = pathLocalFiles + "PropertiesDataBase.properties";

    String newKey;
    String newValue;

    // SystemPropertyes
    public String userName = System.getProperty("user.name");
    public String userHome = System.getProperty("user.home");
    public String osArch = System.getProperty("os.arch");
    public String osName = System.getProperty("os.name");
    public String osVersion = System.getProperty("os.version");
    public String hostName = System.getenv("COMPUTERNAME");

    public LocalDateTime onlineDateTime;
    public LocalDateTime syncDateTime;
    private Map<String, String> config = new HashMap<>();

    private ConcurrentMap<Integer, Action> actionMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, List<Link>> actionLinkMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Type> actionTypeMap = new ConcurrentHashMap<>();

    private ConcurrentMap<Integer, com.admoin.action.type.app.get.Field> actionTypeAppGetField = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Propertie> actionTypeAppGetPropertie = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Upsert> actionTypeDatabaseQueryUpsert = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Select> actionTypeDatabaseQuerySelect = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, AwsS3> actionTypeFileDownloadAwsS3 = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, PublicFile> actionTypeFileDownloadPublicFile = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Exists> actionTypeFileExists = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Remove> actionTypeFileRemove = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Start> actionTypeFileStart = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Json> actionTypeJsonGetJson = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Value> actionTypeJsonGetValue = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Connection> actionTypeNetTestConnection = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Contains> actionTypeStringCompareContains = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Equals> actionTypeStringCompareEquals = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Less> actionTypeStringCompareLess = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, LessVersion> actionTypeStringCompareLessVersion = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Hex> actionTypeStringFormatHex = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Property> actionTypeSystemgetProperty = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Variable> actionTypeSystemgetVariable = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Text> actionTypeUrlGetText = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, Unzip> actionTypeZipUnzip = new ConcurrentHashMap<>();

    public static LocalDateTime timeToStart;
    private static String namePropertyYandexDataBaseReadOnlyConnectionString = "yandex_data_base_read_only_connection_string";
    private static String namePropertyYandexDataBaseReadWriteConnectionString = "yandex_data_base_read_write_connection_string";

    public Host() {
        id = Integer.parseInt(Host.properties.getProperty("id"));

        setDataBaseReadOnly();
        setDataBaseReadWrite();
    }

    public void setName(String newValue) {
        name = newValue;
    }

    public int getNewIdFromDataBase() {
        Log.logger.info("this.getPropertiesNewIdFromDataBase()");
        int lastId;

        String queryGetLastHost = "SELECT `host_id` "
                + "FROM `host` "
                + "ORDER BY `host_id` DESC "
                + "LIMIT 1;";

        try {
            Host.getDataBaseReadWrite().getQuery(queryGetLastHost);
            ResultSetReader resultQuery = Host.getDataBaseReadWrite().getQuery(queryGetLastHost);

            do {
                lastId = (int) resultQuery.getColumn("host_id").getUint64();
            } while (resultQuery.next());
        } catch (Exception e) {
            lastId = 0;
        }

        return lastId;
    }

    public int getId() {
        return id;
    }

    public void setId(int lastId) throws Exception {
        Properties propertiesId = new Properties();

        id = ++lastId;
        Log.logger.info("host.setId(" + lastId + ")");

        String query = "UPSERT INTO `host` "
                + "( `host_id`, `host_datetime`, `host_value` ) "
                + "VALUES (" + id + ",CurrentUtcDatetime(),\"" + hostName
                + "\");";

        Host.getDataBaseReadWrite().sendQuery(query);

        propertiesId.setProperty("id", Integer.toString(id));

        Host.storeProperties(propertiesId, Host.pathPropertiesId);
    }

    public String getFieldValue(String fieldName) {
        Log.logger.info("this.getPropertiesFieldValue(" + fieldName + ")");
        String fieldValue = null;
        try {
            Field field = Host.class.getField(fieldName);
            fieldValue = field.get(this).toString();
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Log.logger.warning(e.getMessage());
        }
        Log.logger.info("this.getPropertiesFieldValue. return: " + fieldValue);
        return fieldValue;
    }

    public boolean isOldVersion(String versionName) {
        boolean oldVersion = false;
        
        String versionFromConfig = null;
        String versionFromHost = null;

        if (getConfig() != null) {
            versionFromConfig = getConfig().get(versionName);
            versionFromHost = Host.properties.getProperty(versionName);
            oldVersion = LessVersion.isLessVersion(versionFromHost, versionFromConfig);
        } else {
            oldVersion = false;
        }

        Log.logger.info("isOldVersion() '" + versionName + "' hostVersion: " + versionFromHost + " configVersion: "
                + versionFromConfig + " result: " + oldVersion);
        return oldVersion;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public void storeToLocalFile(Host host, String outFilePath) {
        Log.logger.info("this.writeObject(%s)".replace("%s", outFilePath));
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        new FileOutputStream(outFilePath));) {
            objectOutputStream.writeObject(host);
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
    }

    public static Host restoreFromLocalFile() {
        String outFilePath = Host.properties.getProperty("host_out_path", "host.out");

        Log.logger.info("this.readObject(%s)".replace("%s", outFilePath));

        Host objectRestored = new Host();
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            Log.logger.info("Get data from file: " + outFilePath);
            try (
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            new FileInputStream(outFilePath));) {
                objectRestored = (Host) objectInputStream.readObject();
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
            }
        } else {
            Log.logger.warning("No found file: " + outFilePath);
        }
        return objectRestored;
    }

    public void getDataFromDataBase() {
        Action.getFromDataBase();
        setActionMap(Action.map);

        Link.getFromDataBase();
        setActionLinkMap(Link.map);

        Type.getFromDataBase();
        setActionTypeMap(Type.map);

        com.admoin.action.type.app.get.Field.getFromDataBase();
        actionTypeAppGetField = com.admoin.action.type.app.get.Field.map;

        com.admoin.action.type.app.get.Propertie.getFromDataBase();
        actionTypeAppGetPropertie = com.admoin.action.type.app.get.Propertie.map;

        com.admoin.action.type.database.query.Upsert.getFromDataBase();
        actionTypeDatabaseQueryUpsert = com.admoin.action.type.database.query.Upsert.map;

        com.admoin.action.type.database.query.Select.getFromDataBase();
        actionTypeDatabaseQuerySelect = com.admoin.action.type.database.query.Select.map;

        com.admoin.action.type.file.download.AwsS3.getFromDataBase();
        actionTypeFileDownloadAwsS3 = com.admoin.action.type.file.download.AwsS3.map;

        com.admoin.action.type.file.download.PublicFile.getFromDataBase();
        actionTypeFileDownloadPublicFile = com.admoin.action.type.file.download.PublicFile.map;

        com.admoin.action.type.file.Exists.getFromDataBase();
        actionTypeFileExists = com.admoin.action.type.file.Exists.map;

        com.admoin.action.type.file.Remove.getFromDataBase();
        actionTypeFileRemove = com.admoin.action.type.file.Remove.map;

        com.admoin.action.type.file.Start.getFromDataBase();
        actionTypeFileStart = com.admoin.action.type.file.Start.map;

        com.admoin.action.type.json.get.Json.getFromDataBase();
        actionTypeJsonGetJson = com.admoin.action.type.json.get.Json.map;
       
        com.admoin.action.type.json.get.Value.getFromDataBase();
        actionTypeJsonGetValue = com.admoin.action.type.json.get.Value.map;

        com.admoin.action.type.net.test.Connection.getFromDataBase();
        actionTypeNetTestConnection = com.admoin.action.type.net.test.Connection.map;

        com.admoin.action.type.string.compare.Contains.getFromDataBase();
        actionTypeStringCompareContains = com.admoin.action.type.string.compare.Contains.map;

        com.admoin.action.type.string.compare.Equals.getFromDataBase();
        actionTypeStringCompareEquals = com.admoin.action.type.string.compare.Equals.map;

        com.admoin.action.type.string.compare.Less.getFromDataBase();
        actionTypeStringCompareLess = com.admoin.action.type.string.compare.Less.map;

        com.admoin.action.type.string.compare.LessVersion.getFromDataBase();
        actionTypeStringCompareLessVersion = com.admoin.action.type.string.compare.LessVersion.map;

        com.admoin.action.type.string.format.Hex.getFromDataBase();
        actionTypeStringFormatHex = com.admoin.action.type.string.format.Hex.map;

        com.admoin.action.type.system.get.Property.getFromDataBase();
        actionTypeSystemgetProperty = com.admoin.action.type.system.get.Property.map;

        com.admoin.action.type.system.get.Variable.getFromDataBase();
        actionTypeSystemgetVariable = com.admoin.action.type.system.get.Variable.map;

        com.admoin.action.type.url.get.Text.getFromDataBase();
        actionTypeUrlGetText = com.admoin.action.type.url.get.Text.map;

        com.admoin.action.type.zip.Unzip.getFromDataBase();
        actionTypeZipUnzip = com.admoin.action.type.zip.Unzip.map;

        App.setUpdateConfig(false);
    }

    public void getDataFromLocalStorage() {
        Action.map = getActionMap();
        Link.map = getActionLinkMap();
        Type.map = getActionTypeMap();

        com.admoin.action.type.app.get.Field.map = actionTypeAppGetField;
        com.admoin.action.type.app.get.Propertie.map = actionTypeAppGetPropertie;
        com.admoin.action.type.database.query.Upsert.map = actionTypeDatabaseQueryUpsert;
        com.admoin.action.type.database.query.Select.map = actionTypeDatabaseQuerySelect;
        com.admoin.action.type.file.download.AwsS3.map = actionTypeFileDownloadAwsS3;
        com.admoin.action.type.file.download.PublicFile.map = actionTypeFileDownloadPublicFile;
        com.admoin.action.type.file.Exists.map = actionTypeFileExists;
        com.admoin.action.type.file.Remove.map = actionTypeFileRemove;
        com.admoin.action.type.file.Start.map = actionTypeFileStart;
        com.admoin.action.type.json.get.Json.map = actionTypeJsonGetJson;
        com.admoin.action.type.json.get.Value.map = actionTypeJsonGetValue;
        com.admoin.action.type.net.test.Connection.map = actionTypeNetTestConnection;
        com.admoin.action.type.string.compare.Contains.map = actionTypeStringCompareContains;
        com.admoin.action.type.string.compare.Equals.map = actionTypeStringCompareEquals;
        com.admoin.action.type.string.compare.Less.map = actionTypeStringCompareLess;
        com.admoin.action.type.string.compare.LessVersion.map = actionTypeStringCompareLessVersion;
        com.admoin.action.type.string.format.Hex.map = actionTypeStringFormatHex;
        com.admoin.action.type.system.get.Property.map = actionTypeSystemgetProperty;
        com.admoin.action.type.system.get.Variable.map = actionTypeSystemgetVariable;
        com.admoin.action.type.url.get.Text.map = actionTypeUrlGetText;
        com.admoin.action.type.zip.Unzip.map = actionTypeZipUnzip;
    }

    public static Properties getProperties() throws Exception {
        Host.getProperties(Host.pathPropertiesDefault, Host.properties);
        Host.getProperties(Host.pathPropertiesCurrent, Host.properties);
        Host.getProperties(Host.pathPropertiesDataBase, Host.properties);
        Host.getProperties(Host.pathPropertiesId, Host.properties);

        return Host.properties;
    }

    static void storePropertiesDataBase() throws Exception {
        File propertiesDataBaseFile = new File(Host.pathPropertiesDataBase);

        if (!propertiesDataBaseFile.exists()) {
            Properties propertiesDataBase = new Properties();

            String connectionStringDataBaseReadOnly = Host.properties
                    .getProperty(namePropertyYandexDataBaseReadOnlyConnectionString);
            String connectionStringDataBaseReadWrite = Host.properties
                    .getProperty(namePropertyYandexDataBaseReadWriteConnectionString);

            propertiesDataBase.setProperty(namePropertyYandexDataBaseReadOnlyConnectionString,
                    connectionStringDataBaseReadOnly);
            propertiesDataBase.setProperty(namePropertyYandexDataBaseReadWriteConnectionString,
                    connectionStringDataBaseReadWrite);

            Host.storeProperties(propertiesDataBase, Host.pathPropertiesDataBase);
        }
    }

    public static void getProperties(String pathFileProperties, Properties propertiesDefault) throws Exception {
        Log.logger.info("this.getProperties(" + pathFileProperties + ", propertiesDefault)");

        if (propertiesDefault == null) {
            Host.properties = new Properties();
        }

        File fileProperties = new File(pathFileProperties);
        if (fileProperties.exists()) {
            try (
                    FileInputStream inputStream = new FileInputStream(fileProperties.getAbsolutePath());) {
                Host.properties.load(inputStream);
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
            }
        }
        Log.logger.info("this.getProperties() End");
    }

    public static void setProperty(String newKey, String newValue) throws Exception {
        Log.logger.info("Host.setProperty(" + newKey + ", " + newValue + ")");
        Host.properties.setProperty(newKey, newValue);
        Log.logger.info("Host.setProperty() End");
    }

    public static void storeProperties(Properties properties, String path) throws Exception {
        File fileProperties = new File(path);
        Log.logger.info("this.storeProperties(" + fileProperties + ", property)");
        try (
                FileOutputStream outputStream = new FileOutputStream(fileProperties.getAbsolutePath());) {
            properties.store(outputStream, "---No Comment---");
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
        Log.logger.info("this.storeProperties() End");
    }

    public boolean isGetNewId() {
        boolean result = false;
        boolean isHostIdEqualsNull = this.isIdEqualsNull();
        boolean isDataBaseReadWriteOpen = Host.getDataBaseReadWrite().isOpen();

        if (isHostIdEqualsNull && isDataBaseReadWriteOpen) {
            result = true;
        }

        return result;
    }

    public boolean isIdEqualsNull() {
        boolean result = false;

        if (Host.properties.getProperty("id").equals("0")) {
            result = true;
        }

        Log.logger.info("Host.properties.getProperty('id').equals('0'): " + result);

        return result;
    }

    public static void writeCurrentDateTimeToDataBaseTable(String tableName) {
        String query = "UPSERT INTO `" + tableName + "` "
                + "( `" +
                tableName + "_host_id" + "`, `" +
                tableName + "_value"
                + "` ) "
                + "VALUES (" + Host.properties.getProperty("id") + ", CurrentUtcDatetime());";

        Host.getDataBaseReadWrite().sendQuery(query);
    }

    public boolean isReady() {
        Boolean result = true;
        boolean isHostIdEqualsNull = this.isIdEqualsNull();
        boolean isDataBaseReadWriteNotOpen = !Host.getDataBaseReadWrite().isOpen();

        if (isHostIdEqualsNull || isDataBaseReadWriteNotOpen) {
            result = false;
        }

        return result;
    }

    public void startActionMap() {
        Log.logger.info("host.startAction()");
        timeToStart = LocalDateTime.now();
        Log.logger.info("Host.timeToStart: " + timeToStart);

        List<Link> linkCheck = new ArrayList<>(Link.map.get(0));
        List<Link> linkCheckNew = new ArrayList<>();

        do {
            linkCheckNew.clear();
            linkCheck.parallelStream().forEach(link -> {
                Action action = new Action(link.getActionFromMap());
                Log.logger.info("Action [id=" + action.getId() + ", typeId=" + action.getTypeId()
                        + ", startIntervalSeconds=" + action.getStartIntervalSeconds()
                        + ", result=" + action.getResult() + ", resultOld=" + action.getResultOld() + ", lastStart="
                        + action.getLastStart() + "]");

                if (action.isTimeToStart()) {
                    try {
                        action.setResultOld(action.getResult());
                        action.start(link.getSource(), this);
                        Log.logger.info("Action. Id: " + action.getId() + ", TypeId: " + action.getTypeId()
                                + ", Result: " + action.getResult() + ", LastStart: " + action.getLastStart());

                        Action.map.put(link.getToId(), action);
                    } catch (Exception e) {
                        Log.logger.warning(e.getMessage());
                    }
                }

                if (action.isCompleted() && Link.map.containsKey(link.getToId())) {
                    linkCheckNew.addAll(getLinkToNextAction(link, action.getResult()));
                }
            });

            linkCheck.clear();
            linkCheck.addAll(linkCheckNew);
            // перебрать все действия на которые указывают ссылки из списка
            // выполнить все дйствия
            // если результат не false добавить в список все ид действий на которые есть
            // ссылки от этого действия
        } while (!linkCheck.isEmpty() && Boolean.FALSE.equals(App.isUpdateConfig()));
    }

    private List<Link> getLinkToNextAction(Link link, String source) {
        List<Link> linkCheckNew = new ArrayList<>();
        Link.map.get(link.getToId()).forEach(linkMapItem -> {
            if (linkMapItem.getFromFalseResult().equals(link.isActionResultEqualsFalse())) {
                linkMapItem.setSource(source);
                linkCheckNew.add(linkMapItem);
            }
        });
        return linkCheckNew;
    }

    public void setConfig(String key, String value) {
        config.put(key, value);
    }
}