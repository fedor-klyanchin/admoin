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

    public String appVersion = App.APP_VERSION;
    public String configVersion = Host.properties.getProperty(App.getConfigVersionPropertyName());
    public static Properties properties;

    public static DataBase dataBaseReadOnly = null;
    public static DataBase dataBaseReadWrite = null;

    // https://docs.oracle.com/javase/tutorial/essential/environment/properties.html
    private static Properties systemPropertyes = System.getProperties();
    private static String userDir = systemPropertyes.getProperty("user.dir");
    private static String fileSeparator = systemPropertyes.getProperty("file.separator");
    private static String pathLocalFiles = userDir + fileSeparator;

    static String pathPropertiesDefault = pathLocalFiles + "PropertiesDefault.properties";
    static String pathPropertiesCurrent = pathLocalFiles + "PropertiesCurrent.properties";

    String newKey;
    String newValue;

    public static ConcurrentMap<Integer, Action> getActionMap() {
        return actionMap;
    }

    public static ConcurrentMap<Integer, List<Link>> getActionLinkMap() {
        return actionLinkMap;
    }

    public static ConcurrentMap<Integer, Type> getActionTypeMap() {
        return actionTypeMap;
    }

    // SystemPropertyes
    public String userName = System.getProperty("user.name");
    public String userHome = System.getProperty("user.home");
    public String osArch = System.getProperty("os.arch");
    public String osName = System.getProperty("os.name");
    public String osVersion = System.getProperty("os.version");
    public String hostName = System.getenv("COMPUTERNAME");

    public LocalDateTime onlineDateTime;
    public LocalDateTime syncDateTime;
    public static Map<String, String> config = new HashMap<>();

    public static ConcurrentMap<Integer, Action> actionMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, List<Link>> actionLinkMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Type> actionTypeMap = new ConcurrentHashMap<>();

    public static ConcurrentMap<Integer, com.admoin.action.type.app.get.Field> actionTypeAppGetField = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Propertie> actionTypeAppGetPropertie = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Upsert> actionTypeDatabaseQueryUpsert = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Select> actionTypeDatabaseQuerySelect = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, AwsS3> actionTypeFileDownloadAwsS3 = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, PublicFile> actionTypeFileDownloadPublicFile = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Exists> actionTypeFileExists = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Remove> actionTypeFileRemove = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Start> actionTypeFileStart = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Json> actionTypeJsonGetJson = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Value> actionTypeJsonGetValue = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Connection> actionTypeNetTestConnection = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Contains> actionTypeStringCompareContains = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Equals> actionTypeStringCompareEquals = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Less> actionTypeStringCompareLess = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, LessVersion> actionTypeStringCompareLessVersion = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Hex> actionTypeStringFormatHex = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Property> actionTypeSystemgetProperty = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Variable> actionTypeSystemgetVariable = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Text> actionTypeUrlGetText = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, Unzip> actionTypeZipUnzip = new ConcurrentHashMap<>();

    ConcurrentMap<Integer, Action> actionDataBase = new ConcurrentHashMap<>();
    List<Link> linkDataBase = new ArrayList<>();

    public Host() {
        id = Integer.parseInt(Host.properties.getProperty("id"));

        String yandexDataBaseReadOnlyConnectionString = Host.properties
                .getProperty("yandex_data_base_read_only_connection_string");
        String yandexDataBaseReadOnlySaKeyFile = Host.properties.getProperty("yandex_data_base_read_only_sa_key_file");
        Host.dataBaseReadOnly = new DataBase(yandexDataBaseReadOnlyConnectionString, yandexDataBaseReadOnlySaKeyFile);

        String yandexDataBaseReadWriteConnectionString = Host.properties
                .getProperty("yandex_data_base_read_write_connection_string");
        String yandexDataBaseReadWriteSaKeyFile = Host.properties
                .getProperty("yandex_data_base_read_write_sa_key_file");
        Host.dataBaseReadWrite = new DataBase(yandexDataBaseReadWriteConnectionString,
                yandexDataBaseReadWriteSaKeyFile);
    }

    public void setName(String newValue) {
        name = newValue;
    }

    public int getNewIdFromDataBase() {
        Log.logger.info("this.getPropertiesNewIdFromDataBase()");
        int lastId = 0;

        String queryGetLastHost = "SELECT `host_id` "
                + "FROM `host` "
                + "ORDER BY `host_id` DESC "
                + "LIMIT 1;";

        Host.dataBaseReadWrite.getQuery(queryGetLastHost);
        ResultSetReader resultQuery = Host.dataBaseReadWrite.getQuery(queryGetLastHost);

        do{
            lastId = (int) resultQuery.getColumn("host_id").getUint64();
        } while (resultQuery.next());

        return lastId;
    }

    public int getId() {
        return id;
    }

    public void setId(int lastId) throws Exception {
        id = ++lastId;
        Log.logger.info("host.setId(" + lastId + ")");

        String query = DataBase.getQueryVariableDateTimeMoscow() + "UPSERT INTO `host` "
                + "( `host_id`, `host_datetime`, `host_value` ) "
                + "VALUES (" + id + ",$currentDateTimeMoscow,\"" + hostName
                + "\");";

        Host.dataBaseReadWrite.sendQuery(query);
        Host.setProperty("id", Integer.toString(id));
        Host.storeProperties();
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

    public static boolean isOldVersion(String versionName) {
        boolean oldVersion = false;
        String configVersion = null;
        String hostVersion = null;

        if (Host.config != null) {
            configVersion = Host.config.get(versionName);
            hostVersion = Host.properties.getProperty(versionName);
            oldVersion = LessVersion.isLessVersion(hostVersion, configVersion);
        } else {
            oldVersion = false;
        }

        Log.logger.info("isOldVersion() '" + versionName + "' hostVersion: " + hostVersion + " configVersion: "
                + configVersion + " result: " + oldVersion);
        return oldVersion;
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

    public Host restoreFromLocalFile() {
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

    public static void getDataFromDataBase() {
        actionMap = Action.getFromDataBase();
        actionLinkMap = Link.getFromDataBase();
        actionTypeMap = Type.getFromDataBase();

        actionTypeAppGetField = com.admoin.action.type.app.get.Field.getFromDataBase();
        actionTypeAppGetPropertie = com.admoin.action.type.app.get.Propertie.getFromDataBase();
        actionTypeDatabaseQueryUpsert = com.admoin.action.type.database.query.Upsert.getFromDataBase();
        actionTypeDatabaseQuerySelect = com.admoin.action.type.database.query.Select.getFromDataBase();
        actionTypeFileDownloadAwsS3 = com.admoin.action.type.file.download.AwsS3.getFromDataBase();
        actionTypeFileDownloadPublicFile = com.admoin.action.type.file.download.PublicFile.getFromDataBase();
        actionTypeFileExists = com.admoin.action.type.file.Exists.getFromDataBase();
        actionTypeFileRemove = com.admoin.action.type.file.Remove.getFromDataBase();
        actionTypeFileStart = com.admoin.action.type.file.Start.getFromDataBase();
        actionTypeJsonGetJson = com.admoin.action.type.json.get.Json.getFromDataBase();
        actionTypeJsonGetValue = com.admoin.action.type.json.get.Value.getFromDataBase();
        actionTypeNetTestConnection = com.admoin.action.type.net.test.Connection.getFromDataBase();
        actionTypeStringCompareContains = com.admoin.action.type.string.compare.Contains.getFromDataBase();
        actionTypeStringCompareEquals = com.admoin.action.type.string.compare.Equals.getFromDataBase();
        actionTypeStringCompareLess = com.admoin.action.type.string.compare.Less.getFromDataBase();
        actionTypeStringCompareLessVersion = com.admoin.action.type.string.compare.LessVersion.getFromDataBase();
        actionTypeStringFormatHex = com.admoin.action.type.string.format.Hex.getFromDataBase();
        actionTypeSystemgetProperty = com.admoin.action.type.system.get.Property.getFromDataBase();
        actionTypeSystemgetVariable = com.admoin.action.type.system.get.Variable.getFromDataBase();
        actionTypeUrlGetText = com.admoin.action.type.url.get.Text.getFromDataBase();
        actionTypeZipUnzip = com.admoin.action.type.zip.Unzip.getFromDataBase();

        App.setUpdateConfig(false);
    }

    public static Properties getProperties() throws Exception {
        Host.getProperties(Host.pathPropertiesDefault, Host.properties);
        Host.getProperties(Host.pathPropertiesCurrent, Host.properties);
        return Host.properties;
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
        Host.getProperties(Host.pathPropertiesCurrent, Host.properties);
        Host.properties.setProperty(newKey, newValue);
        Log.logger.info("Host.setProperty() End");
    }

    public static void storeProperties() throws Exception {
        File fileProperties = new File(Host.pathPropertiesCurrent);
        Log.logger.info("this.storeProperties(" + fileProperties + ", property)");
        try (
                FileOutputStream outputStream = new FileOutputStream(fileProperties.getAbsolutePath());) {
            Host.properties.store(outputStream, "---No Comment---");
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
        Log.logger.info("this.storeProperties() End");
    }

    public boolean isGetNewId() {
        boolean result = false;
        boolean isHostIdEqualsNull = this.isIdEqualsNull();
        boolean isDataBaseReadWriteOpen = Host.dataBaseReadWrite.isOpen();

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
        String query = DataBase.getQueryVariableDateTimeMoscow() + "UPSERT INTO `" + tableName + "` "
                + "( `" +
                tableName + "_host_id" + "`, `" +
                tableName + "_datetime"
                + "` ) "
                + "VALUES (" + Host.properties.getProperty("id") + ",$currentDateTimeMoscow);";

        Host.dataBaseReadWrite.sendQuery(query);
    }

    public boolean isReady() {
        Boolean result = true;
        boolean isHostIdEqualsNull = this.isIdEqualsNull();
        boolean isDataBaseReadWriteOpen = Host.dataBaseReadWrite.isOpen();

        if (isHostIdEqualsNull || !isDataBaseReadWriteOpen) {
            result = false;
        }

        return result;
    }

    public void startActionMap() {
        Log.logger.info("host.startAction()");

        List<Link> linkCheck = Host.actionLinkMap.get(0);
        List<Link> linkCheckNew = new ArrayList<>();

        do {
            linkCheckNew.clear();
            linkCheck.parallelStream().forEach(link -> {
                Action action = link.getActionFromMap();

                if (action.isTimeToStart()) {
                    try {
                        action.setResultOld(action.getResult());
                        action.start(link.getSource(), this);
                        Log.logger.info("Action. Id: " + action.getId() + ", TypeId: " + action.getTypeId() + ", Result: " + action.getResult());
                        
                        Host.actionMap.put(link.getToId(), action);
                    } catch (Exception e) {
                        Log.logger.warning(e.getMessage());
                    }
                }

                if (action.isCompleted() && Host.actionLinkMap.containsKey(link.getToId())) {
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
        Host.actionLinkMap.get(link.getToId()).forEach(linkMapItem -> {
            if (linkMapItem.getFromFalseResult().equals(link.isActionResultEqualsFalse())) {
                linkMapItem.setSource(source);
                linkCheckNew.add(linkMapItem);
            }
        });
        return linkCheckNew;
    }
}