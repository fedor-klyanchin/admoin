package com.admoin.action.type.net.test;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.admoin.DataBase;
import com.admoin.Host;
import com.admoin.Log;
import com.admoin.action.type.Type;

import tech.ydb.table.result.ResultSetReader;

public class Connection implements Serializable {
    private static final long serialVersionUID = 8L;

    private static String tablePath = "action/type/net/test/net_test_connection";
    private static String tableName = Type.getTableName(tablePath);

    public static ConcurrentMap<Integer, Connection> map = new ConcurrentHashMap<>();

    private String address;
    private int port;

    public Connection(
            String address,
            int port) {
        this.address = address;
        this.port = port;
    }

    public static void getFromDataBase() {
        Log.logger.info("ActionFileDownload.getFromYandexDataBase()");

        Log.logger.info("new ActionFileDownload[]");
        ConcurrentHashMap<Integer, Connection> actionMap = new ConcurrentHashMap<>();

        ResultSetReader result = Host.getDataBaseReadOnly().getQuery("SELECT * FROM `?`".replace("?", tablePath));

        do {
            int actionId = DataBase.getColumnInt(result, "?_action_id".replace("?", tableName));
            String address = DataBase.getColumnString(result, "?_address".replace("?", tableName));
            int port = DataBase.getColumnInt(result, "?_port".replace("?", tableName));

            Connection action = new Connection(address, port);

            actionMap.put(actionId, action);

            Log.logger.info("New action [actionId=" + actionId + ", address=" + address + ", port=" + port + "]");
        } while (result.next());

        Connection.map = actionMap;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    private String getLogMessageForIsConnected() {
        return "Action.isConnected( Name: " + address + ", Port: " + port + ")";
    }

    public Boolean isConnected() throws UnknownHostException {
        Log.logger.info(getLogMessageForIsConnected() + " Start");
        Boolean socketIsConnected = false;
        // https://docs.oracle.com/javase/7/docs/api/java/net/InetAddress.html
        InetAddress name = InetAddress.getByName(address);
        try (
            // https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String)
            // https://stackoverflow.com/questions/5585779/how-do-i-convert-a-string-to-an-int-in-java

            // https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#isConnected()
            Socket socket = new Socket(name, port);) {
            socketIsConnected = socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Log.logger.info(getLogMessageForIsConnected() + " Break");
            Log.logger.warning(e.getMessage());
        }

        Log.logger.info(getLogMessageForIsConnected() + " Return: " + socketIsConnected);

        return socketIsConnected;
    }

    public String start() throws UnknownHostException {
        return Boolean.toString(this.isConnected());
    }
}