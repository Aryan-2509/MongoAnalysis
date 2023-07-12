package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DetermineDocumentSize implements DetermineDocumentSizeService{
    private static final Logger logger = Logger.getLogger(DetermineDocumentSize.class.getName());

    private static ExcelWriter excelWriter = new ExcelWriter();

    private static boolean isMongoDBIsoDate(String value) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String determineDataType(Object fieldValue) {
        if (fieldValue == null) {
            return "null";
        } else if (fieldValue instanceof Boolean) {
            return "Boolean";
        } else if (fieldValue instanceof Number) {
            if (fieldValue instanceof Double || fieldValue instanceof Float) {
                return "Double";
            }
            return "Number";
        } else if (fieldValue instanceof JSONObject) {
            return "Object";
        } else if (fieldValue instanceof JSONArray) {
            return "Array";
        } else if (fieldValue instanceof String) {
            String strValue = (String) fieldValue;
            if (isMongoDBIsoDate(strValue)) {
                return "Date";
            }
            return "String";
        } else {
            logger.log(Level.SEVERE, "ERROR: Unexpected data type");
            throw new RuntimeException("ERROR: Unexpected data type");
        }
    }

    private static int processJSONArray(JSONArray jsonArray) throws JSONException {
        int size = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            Object arrayElement = jsonArray.get(i);
            String datatype = determineDataType(arrayElement);

            switch (datatype) {
                case "Object":
                    JSONObject jsonObject = (JSONObject) arrayElement;
                    int objSize = processJSONObject(jsonObject) + 7 + 1;
                    size += objSize;
                    break;
                case "Number":
                    size += 7;
                    break;
                case "Boolean":
                    size += 4;
                    break;
                case "Double":
                    size += 11;
                    break;
                case "String":
                    String fieldValue = arrayElement.toString();
                    size += 8 + fieldValue.length();
                    break;
                case "null":
                    size += 3;
                    break;
                case "Date":
                    size += 11;
                    break;
                case "Array":
                    JSONArray nestedArray = (JSONArray) arrayElement;
                    size += processJSONArray(nestedArray) + 7 + 1;
                    break;
            }
        }
        return size;
    }

    private static int processJSONObject(JSONObject jsonObject) throws JSONException {
        int size = 0;

        for (Iterator it = jsonObject.keys(); it.hasNext(); ) {
            Object key = it.next();
            String fieldName = (String) key;
            Object fieldValue = jsonObject.get(fieldName);
            String dataType = determineDataType(fieldValue);

            switch (dataType) {
                case "String":
                    String fieldVal = (String) fieldValue;
                    size += 7 + fieldName.length() + fieldVal.length();
                    break;
                case "Boolean":
                    size += fieldName.length() + 3;
                    break;
                case "Number":
                    size += fieldName.length() + 6;
                    break;
                case "Object":
                    int objSize = processJSONObject((JSONObject) fieldValue);
                    size += objSize + fieldName.length() + 7;
                    break;
                case "Array":
                    int arraySize = processJSONArray((JSONArray) fieldValue);
                    size += arraySize + fieldName.length() + 7;
                    break;
                case "null":
                    size += fieldName.length() + 2;
                    break;
                case "Double":
                    size += fieldName.length() + 10;
                    break;
                case "Date":
                    size += fieldName.length() - 2;
                    break;
            }
        }
        return size;
    }

    private static int findSize(String url) {
        ReadLink read = new ReadLink();
        JSONObject jsonObject = read.getJSONObject(url);

        int size;
        try {
            size = processJSONObject(jsonObject);
        } catch (JSONException e) {
            logger.log(Level.SEVERE, "ERROR: ", e);
            throw new RuntimeException(e);
        }

        return size;
    }

    @Override
    public void findDocumentSize(String url) {

        int documentSize = findSize(url) + 5;
        excelWriter.WriteInteger(documentSize);
    }

    @Override
    public void findOverhead(String url) {

        int overhead =  findSize(url);
        excelWriter.WriteInteger(overhead);
    }
}
