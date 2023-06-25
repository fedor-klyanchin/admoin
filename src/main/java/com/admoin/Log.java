package com.admoin;

import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;
import java.io.IOException;

public class Log {
    public static Logger logger;
    static String logName;
    static String logPath;

    public static void create() {
        if (Log.logName == null) {
            Log.create("Log", "Log.log");
            Log.logger.info("Start");
        }
    }

    public static void create(Properties properties) {
        String logName = properties.getProperty("log_name", "Log");
        String logPath = properties.getProperty("log_path", "log.log");
        if (!Log.logName.equals(logName)) {
            Log.logger.info("Main log file: " + logPath);
            Log.create(logName, logPath);
            Log.logger.info("Start");
        }
    }

    public static void create(String logName, String logPath) {
        Log.logName = logName;
        Log.logPath = logPath;

        File logFile = new File(logPath);
        if (logFile.exists()) {
            logFile.delete();
        }

        File logLckFile = new File(logPath + ".lck");
        if (logLckFile.exists()) {
            logLckFile.delete();
        }

        logger = Logger.getLogger(logName);
        try {
            // This block configure the logger with handler and formatter
            FileHandler FileHandler = new FileHandler(logPath);
            logger.addHandler(FileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler.setFormatter(formatter);
        } catch (IOException e) {
            Log.logger.warning(e.getMessage());
        }
    }
}