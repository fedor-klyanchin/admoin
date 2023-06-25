package com.admoin.action.type.string.compare;

import com.admoin.Log;

public abstract class Compare {
    Boolean result = false;
    protected String controlValue;
    protected String currentValue;

    protected static String tablePath;

    Compare(String controlValue) {
        this.controlValue = controlValue;
    }

    public String getControlValue() {
        return controlValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public Boolean getResult() {
        return result;
    }

    protected void writeLogMessage() {
        Log.logger.info("Compare [result=" + result + ", controlValue=" + controlValue + ", currentValue=" + currentValue + "]");
    }

    protected abstract void compare();

    public String start(String source) {
        this.currentValue = source;
        this.compare();
        this.writeLogMessage();
        return Boolean.toString(result);
    }
    
    /*

    private static ConcurrentHashMap<String, String> extracted(ConcurrentHashMap<String, String> columnMap) {
        return columnMap;
    }; 
        ConcurrentHashMap<String, String> columnMap = new ConcurrentHashMap<>();
        do {
            actionStringCompareMap.put(actionId, actionStringCompare);
        } while (result.next());

    public void getTable(ResultSetReader result, ConcurrentMap<String, String> columnMap) {
        columnMap.forEach((conlumnType, columnName) -> {
                switch (conlumnType) {
                    case "int":
                        int id = DataBase.getColumnInt(result, columnName);
                    case "String":
                        return DataBase.getColumnString(result, columnName);
                    case "Boolean":
                        return DataBase.getColumnBoolean(result, columnName);
                    default:
                        break;
                }
            });
            actionStringCompareMap.put(id, actionStringCompare);
    }
    getTable(result, columnMap).forEach()
 */
}