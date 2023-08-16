package com.admoin;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ForkJoinPool;

import com.admoin.action.type.net.test.Connection;

import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.transaction.TxControl;
import tech.ydb.table.transaction.TxControl.TxSerializableRw;
import tech.ydb.core.Result;

public class DataBase implements Serializable {
    private String connectionConfigFile;
    private String connectionString;
    private String saKeyFile;
    GrpcTransport transport;
    TableClient tableClient;
    String query;

    public DataBase(String connectionString, String saKeyFile) {
        this.setConnectionString(connectionString);
        this.setSaKeyFile(saKeyFile);
        Log.logger.info("New DataBase. connectionString: " + connectionString + ", saKeyFile: " + saKeyFile);
    }

    public String getSaKeyFile() {
        return saKeyFile;
    }

    public String getConnectionConfigFile() {
        return connectionConfigFile;
    }

    public void setSaKeyFile(String saKeyFile) {
        this.saKeyFile = saKeyFile;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public static void closeAll() {
        Host.getDataBaseReadOnly().close();
        Host.getDataBaseReadWrite().close();
    }

    public static void openAll() {
        Host.getDataBaseReadOnly().openIfClose();
        Host.getDataBaseReadWrite().openIfClose();
    }

    public void openIfClose() {
        if (Boolean.FALSE.equals(this.isOpen())) {
            this.open();
            Log.logger.info("DataBase.open() opened successfully");
        } else {
            Log.logger.info("DataBase.open() already open");
        }
    }

    public static Boolean isConnected() throws UnknownHostException {
        String yandexDataBaseServerlessNameString = Host.properties.getProperty("yandex_data_base_serverless_name");
        String yandexDataBaseServerlessPortString = Host.properties.getProperty("yandex_data_base_serverless_port");
        Connection actionConnectedToYandexDataBaseServerless = new Connection(yandexDataBaseServerlessNameString, Integer.parseInt(yandexDataBaseServerlessPortString));
        return actionConnectedToYandexDataBaseServerless.isConnected();
    }

    public static Boolean isConnected(String yandexDataBaseServerlessNameString,
            String yandexDataBaseServerlessPortString) {
        Log.logger.info("DataBase.isConnected( Name: " + yandexDataBaseServerlessNameString + ", Port: "
                + yandexDataBaseServerlessPortString + ")");
        Boolean socketIsConnected = false;
        // https://docs.oracle.com/javase/7/docs/api/java/net/InetAddress.html
        // https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String)
        // https://stackoverflow.com/questions/5585779/how-do-i-convert-a-string-to-an-int-in-java
        // https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#isConnected()
        InetAddress yandexDataBaseServerlessName = null;
        int yandexDataBaseServerlessPort = 0;

        try {
            yandexDataBaseServerlessName = InetAddress.getByName(yandexDataBaseServerlessNameString);
            yandexDataBaseServerlessPort = Integer.parseInt(yandexDataBaseServerlessPortString);
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        try (
            Socket socket = new Socket(yandexDataBaseServerlessName, yandexDataBaseServerlessPort);
        ) {
            socketIsConnected = socket.isConnected();
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        Log.logger.info("DataBase.isConnected() Return: " + socketIsConnected);
        return socketIsConnected;
    }

    public void open() {
        Log.logger.info("DataBase.open() " + getConnectionConfigFile() + ", " + getSaKeyFile());
        AuthProvider authProvider = CloudAuthHelper.getServiceAccountFileAuthProvider(getSaKeyFile());
        transport = GrpcTransport.forConnectionString(getConnectionString())
                .withSecureConnection()
                .withAuthProvider(authProvider) // Or this method could not be called at all
                .build();
        tableClient = TableClient.newClient(transport)// https://ydb.tech/ru/docs/concepts/limits-ydb
                // 10 - minimum number of active sessions to keep in the pool during the cleanup
                // 500 - maximum number of sessions in the pool
                .sessionPoolSize(10, 500)// https://ydb.tech/ru/docs/reference/ydb-sdk/recipes/session-pool-limit
                .build();
        Log.logger.info("DataBase.open() opened successfully");
    }

    public Boolean isOpen() {
        if (this.tableClient != null) {
            Log.logger.info("DataBase is open");
            return true;
        } else {
            Log.logger.info("DataBase is not open");
            return false;
        }
    }

    public ResultSetReader getQuery(String query) {
        Log.logger.info("DataBase.getQuery(query) Start");
        ResultSetReader resultSet = null;
        try {
            SessionRetryContext ctx = SessionRetryContext.create(tableClient)
                    .executor(ForkJoinPool.commonPool())
                    .maxRetries(5)
                    .build();

            Result<DataQueryResult> result = ctx.supplyResult(session -> {
                TxControl<TxSerializableRw> txControl = TxControl.serializableRw()
                        .setCommitTx(true);
                return session.executeDataQuery(query, txControl);
            }).join();

            DataQueryResult dataQueryResult = result.getValue();
            resultSet = dataQueryResult.getResultSet(0);
            Log.logger.info("DataBase.getQuery(query) End");
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }

        if (resultSet == null || !resultSet.next()) {
            throw new RuntimeException("not found first_aired");
        }

        return resultSet;
    }

    public void set(String query) {
        Log.logger.info("DataBase.set(query) Start");
        // Begin new transaction with SerializableRW mode
        TxControl<TxSerializableRw> txControl = TxControl.serializableRw().setCommitTx(true);
        SessionRetryContext ctx = SessionRetryContext.create(tableClient)
                .executor(ForkJoinPool.commonPool())
                .maxRetries(5)
                .build();
        // Executes data query with specified transaction control settings.
        ctx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();

        Log.logger.info("DataBase.set(query) End");
    }

    public String sendQuery(String query) {
        String result = "false";
        try {
            this.set(query);
            result = "true";
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
            App.setExitApp(true);
            this.close();
            this.open();
        }
        return result;
    }

    public void close() {
        try {
            Log.logger.info("DataBase.close() Start");
            if (this.tableClient != null) {
                tableClient.close();
                transport.close();
                Log.logger.info("DataBase.close() Close");
            }
            Log.logger.info("DataBase.close() End");
        } catch (Exception e) {
            Log.logger.warning(e.getMessage());
        }
    }

    public static int getColumnInt(ResultSetReader result, String columnName) {
        return (int) result.getColumn(columnName).getUint64();
    }

    public static String getColumnString(ResultSetReader result, String columnName) {
        return result.getColumn(columnName).getText();
    }

    public static Boolean getColumnBoolean(ResultSetReader result, String columnName) {
        return result.getColumn(columnName).getBool();
    }

    public static String getQueryVariableDateTimeMoscow() {
        return "$currentUtcDatetime = CurrentUtcDatetime();" +
                "$hourUtc = DateTime::GetHour($currentUtcDatetime);" +
                "$hourMoscow = $hourUtc+3;" +
                "$hour = CAST($hourMoscow AS Uint8);" +
                "$currentDateTimeMoscow = DateTime::MakeDatetime(DateTime::Update($currentUtcDatetime, $hour as Hour));";
    }
}
