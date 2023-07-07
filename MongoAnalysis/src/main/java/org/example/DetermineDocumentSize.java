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

    private static boolean isMongoDBIsoDate(String value) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private static String determineDataType(Object fieldValue) {

        if(fieldValue == null){
            return "null";
        }
        if (fieldValue instanceof Boolean) {
            return "Boolean";
        }
        else if (fieldValue instanceof Number) {
            if (fieldValue instanceof Double || fieldValue instanceof Float) {
                return "Double";
            }
            return "Number";
        }
        else if (fieldValue instanceof JSONObject) {
            return "Object";
        }
        else if(fieldValue instanceof JSONArray) {
            return "Array";
        }
        else if (fieldValue instanceof String) {
            String strValue = (String) fieldValue;
            if (isMongoDBIsoDate(strValue)) {
                return "Date";
            }
            return "String";
        }
        else {
            logger.log(Level.SEVERE, "ERROR: Unexpected data type");
            throw new RuntimeException("ERROR: Unexpected data type");
        }
    }

    private static int processJSONArray(JSONArray jsonArray) throws JSONException {

        int size = 0;

        for(int i = 0 ; i < jsonArray.length() ; i++){
            Object arrayElement = jsonArray.get(i);
            String datatype = determineDataType(arrayElement);

            switch (datatype) {
                case "Object" -> {
                    JSONObject jsonObject = (JSONObject) arrayElement;
                    int objSize = processJSONObject(jsonObject) + 7 + 1;
                    size += objSize;
                }
                case "Number" -> size += 7;
                case "Boolean" -> size += 4;
                case "Double" -> size += 11;
                case "String" -> {
                    String fieldValue = arrayElement.toString();
                    size += 8 + fieldValue.length();
                }
                case "null" -> size += 3;
                case "Date" -> size += 11;
                case "Array" -> {
                    JSONArray nestedArray = (JSONArray) arrayElement;
                    size += processJSONArray(nestedArray) + 7 + 1;
                }
            }
        }

        return size;
    }

    private static int processJSONObject(JSONObject jsonObject) throws JSONException {
        int size = 0;

        for(Iterator it = jsonObject.keys(); it.hasNext(); ) {
            Object key = it.next();
            String fieldName = (String) key;
            Object fieldValue = jsonObject.get((String) key);
            String dataType = determineDataType(fieldValue);

            switch (dataType) {
                case "String" -> {
                    String fieldVal = (String) jsonObject.get(fieldName);
                    size += 7 + fieldName.length() + fieldVal.length();
                }
                case "Boolean" -> size += fieldName.length() + 3;
                case "Number" -> size += fieldName.length() + 6;
                case "Object" -> {
                    int objSize = processJSONObject((JSONObject) fieldValue);
                    size += objSize + fieldName.length() + 7;
                }
                case "Array" -> {
                    int arraySize = processJSONArray((JSONArray) fieldValue);
                    size += arraySize + fieldName.length() + 7;
                }
                case "null" -> size += fieldName.length() + 2;
                case "Double" -> size += fieldName.length() + 10;
                case "Date" -> size += fieldName.length() - 2;
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
            logger.log(Level.SEVERE, "An error occurred", e);
            throw new RuntimeException(e);
        }

        return size;
    }
    @Override
    public int findDocumentSize(String url) {
        return findSize(url) + 5;
    }

    @Override
    public int findOverhead(String url) {
        return findSize(url);
    }
}
