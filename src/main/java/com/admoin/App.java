package com.admoin;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.admoin.action.Action;
import com.admoin.action.Link;
import com.admoin.action.type.Type;
import com.admoin.action.type.file.Start;

public class App {
    public static final String APP_VERSION = "2.0.12";

    static Map<String, String> config = new HashMap<>();
    static boolean oldVersionApp;
    static boolean oldVersionConfig;
    static boolean exitApp;
    private static String appVersionPropertyName = "app_version";
    private static String configVersionPropertyName = "config_version";
    private static Boolean retryExecution;
    static Host host;

    public static String getAppVersionPropertyName() {
        return appVersionPropertyName;
    }

    public static String getConfigVersionPropertyName() {
        return configVersionPropertyName;
    }

    public static String getAppVersion() {
        return APP_VERSION;
    }

    public static Map<String, String> getConfig() {
        return config;
    }

    public static boolean isOldVersionApp() {
        return oldVersionApp;
    }

    public static boolean isOldVersionConfig() {
        return oldVersionConfig;
    }

    public static boolean isExitApp() {
        return exitApp;
    }

    public static boolean isUpdateConfig() {
        return updateConfig;
    }

    private static boolean updateConfig;

    private static String dataBaseReadOnlyConnectionString;

    public static void main(String[] args) throws Exception {
        Log.create();

        Host.getProperties();
        Host.storePropertiesDataBase();

        Log.create(Host.properties);

        dataBaseReadOnlyConnectionString = Host.properties.getProperty("yandex_data_base_read_only_connection_string");

        host = Host.restoreFromLocalFile();
        Host.getProperties();

        Host.setProperty(App.getAppVersionPropertyName(), APP_VERSION);
        App.storeHostData(host);
        oldVersionApp = host.isOldVersion(App.getAppVersionPropertyName());
        oldVersionConfig = host.isOldVersion(App.getConfigVersionPropertyName());

        if (Integer.parseInt(getPropertyHostId()) == (host.getId())) {
            startMainRunLoop(host);
        } else {
            Log.logger
                    .warning("Failed read from Host.properties. The current value of app_version: " + host.appVersion);
        }
        Log.logger.info("Exit current app process");
        Runtime.getRuntime().exit(1);
    }

    private static void startMainRunLoop(Host host) throws InterruptedException {
        do {
            try {
                if (Boolean.TRUE.equals(DataBase.isConnected())) {
                    DataBase.openAll();

                    if (host.isGetNewId()) {
                        host.setId(host.getNewIdFromDataBase());
                        Host.getProperties();
                    }

                    if (host.isReady()) {
                        host.onlineDateTime = LocalDateTime.now();
                        Host.writeCurrentDateTimeToDataBaseTable("online_datetime");

                        startSync(host);
                        host.startActionMap();

                        host.syncDateTime = LocalDateTime.now();
                        Host.writeCurrentDateTimeToDataBaseTable("sync_datetime");
                    }
                } else {
                    Log.logger.warning("No connection to data base");
                }
                App.storeHostData(host);
                Log.logger.info("End current cycle");
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
                Log.logger.warning("Break current cycle");
                App.exitApp = true;
                DataBase.closeAll();
            }

            App.isRetryExecution();
            if (Boolean.TRUE.equals(App.retryExecution)) {
                App.sleep();
            }
        } while (Boolean.TRUE.equals(App.retryExecution));
        DataBase.closeAll();
    }

    private static void startSync(Host host) throws Exception {
        if (Boolean.TRUE.equals(Host.dataBaseReadOnly.isOpen())) {
            config = Config.getFromDataBase();

            if (config != null) {
                setConfig(host);
                App.storeHostData(host);
            }
        }

        if (App.isGetDataFromDataBase()) {
            host.getDataFromDataBase();

            Host.storeProperties(Host.properties, Host.pathPropertiesCurrent);
            App.storeHostData(host);
        } else {
            host.getDataFromLocalStorage();
        }
    }

    private static void setConfig(Host host) {
        host.setConfig(config);

        oldVersionApp = host.isOldVersion(App.getAppVersionPropertyName());
        oldVersionConfig = host.isOldVersion(App.getConfigVersionPropertyName());

        host.setConfig("config_version", config.get(App.getConfigVersionPropertyName()));

        config.forEach((key, value) -> {
            try {
                Host.setProperty(key, value);
            } catch (Exception e) {
                Log.logger.warning(e.getMessage());
            }
        });

        App.storeHostData(host);
    }

    private static void storeHostData(Host host) {
        try {
            host.storeToLocalFile(host, Host.properties.getProperty("host_out_path", "host.out"));
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
    }

    private static String getPropertyHostId() {
        return Host.properties.getProperty("id");
    }

    static boolean isGetDataFromDataBase() {
        return App.updateConfig || oldVersionApp || oldVersionConfig || Action.map.size() == 0 || Link.map.size() == 0
                || Type.map.size() == 0 ||
                isDatabaseConnectionStringChanged();
    }

    static boolean isDatabaseConnectionStringChanged() {
        return dataBaseReadOnlyConnectionString == null || !dataBaseReadOnlyConnectionString
                .equals(Host.properties.getProperty("yandex_data_base_read_only_connection_string", ""));
    }

    public static String testStart() {
        Start actionAppStart = new Start("notepad.exe");
        return actionAppStart.start();
    }

    public static String getDataBaseReadOnlyConnectionString() {
        return dataBaseReadOnlyConnectionString;
    }

    public static void setDataBaseReadOnlyConnectionString(String dataBaseReadOnlyConnectionString) {
        App.dataBaseReadOnlyConnectionString = dataBaseReadOnlyConnectionString;
    }

    public static void restart() {
        String appExePath = Host.properties.getProperty("app_exe", "AdminConsole.exe");
        File appExeFile = new File(appExePath);
        if (appExeFile.exists()) {
            Log.logger.info("Start new app process: " + appExePath);
            Start actionAppStart = new Start(appExePath);
            actionAppStart.start();
        } else {
            Log.logger.warning("No found: " + appExePath);
        }
    }

    static Boolean isRetryExecution() {
        Boolean hostConfigNotNull;
        if (host != null) {
            hostConfigNotNull = host.getConfig() != null;
        } else {
            hostConfigNotNull = false;
        }

        Log.logger.info("host.getConfig() != null: " + hostConfigNotNull);
        Log.logger.info("App.exitApp: " + App.exitApp);

        boolean useLotMemory = App.isUseLotMemory();
        App.retryExecution = hostConfigNotNull && !oldVersionApp && !useLotMemory && !App.exitApp;

        if (Boolean.TRUE.equals(App.retryExecution)) {
            Log.logger.info("Retry execution");
        } else {
            Log.logger.info("Do not retry execution");
        }

        return App.retryExecution;
    }

    static Boolean isUseLotMemory() {
        long totalMemory;

        Log.logger.info("maxMemory: " + Runtime.getRuntime().maxMemory());
        Log.logger.info("freeMemory: " + Runtime.getRuntime().freeMemory());
        Log.logger.info("totalMemory: " + Runtime.getRuntime().totalMemory());

        int appTotalUsedMemoryLimitBytes = 0;
        if (host != null && host.getConfig().containsKey("app_total_used_memory_limit_bytes")) {
            appTotalUsedMemoryLimitBytes = Integer.parseInt(host.getConfig().get("app_total_used_memory_limit_bytes"));
        } else {
            appTotalUsedMemoryLimitBytes = 300000000;
        }

        totalMemory = Runtime.getRuntime().totalMemory();

        boolean checkTotalMemory = totalMemory > appTotalUsedMemoryLimitBytes;

        if (checkTotalMemory) {
            Log.logger.info("Uses a lot of memory: " + totalMemory + " > " + appTotalUsedMemoryLimitBytes);
        } else {
            Log.logger.info("Normal memory usage: " + totalMemory + " < " + appTotalUsedMemoryLimitBytes);
        }

        return checkTotalMemory;
    }

    public static void sleep() throws InterruptedException {
        // TimeUnit.SECONDS.sleep(60);//https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
        int sleepSeconds = 0;
        sleepSeconds = Integer.parseInt(config.get("app_resync_timeout_seconds"));

        if (sleepSeconds == 0) {
            sleepSeconds = 3600;
        }

        App.sleep(sleepSeconds);
    }

    public static void sleep(int sleepSeconds) throws InterruptedException {
        // https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
        Log.logger.info("Sleep " + sleepSeconds + " seconds");
        TimeUnit.SECONDS.sleep(sleepSeconds);
    }

    public static String getUuid() {
        UUID randomUUID = null;
        String dataAndUuid = null;

        LocalDateTime localDateTimeNow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String localDateTimeNowFormat = localDateTimeNow.format(formatter);

        randomUUID = java.util.UUID.randomUUID();
        dataAndUuid = localDateTimeNowFormat + "-" + randomUUID;
        Log.logger.info("UUID: " + dataAndUuid);

        return dataAndUuid;
    }

    static void setExitApp(Boolean value) {
        App.exitApp = value;
    }

    public static void setUpdateConfig(boolean b) {
        App.updateConfig = b;
    }

    public static Boolean getRetryExecution() {
        return retryExecution;
    }
}