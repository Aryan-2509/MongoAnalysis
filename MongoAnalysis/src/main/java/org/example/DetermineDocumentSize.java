package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class DetermineDocumentSize {

    private static boolean isDateFormat(String value) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private static String determineDataType(Object fieldValue) {
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
        else if (fieldValue instanceof String && isDateFormat((String) fieldValue)) {
            return "Date";
        }
        else if (fieldValue instanceof String) {
            return "String";
        }
        else {
            return "null";
        }
    }

    private static int processJSONArray(JSONArray jsonArray) throws JSONException {

        int size = 0;

        for(int i = 0 ; i < jsonArray.length() ; i++){
            Object arrayElement = jsonArray.get(i);
            String datatype = determineDataType(arrayElement);

            if (datatype.equals("Object")) {
                JSONObject jsonObject = (JSONObject) arrayElement;
                int objSize = processJSONObject(jsonObject) + 7 + 1;
                size += objSize;
            }
            else if(datatype.equals("Number")) {
                size += 7;
            }
            else if(datatype.equals("Boolean")) {
                size += 4;
            }
            else if(datatype.equals("Double")) {
                size += 11;
            }
            else if(datatype.equals("String")) {
                String fieldValue = arrayElement.toString();
                size += 8 + fieldValue.length();
            }
            else if(datatype.equals("null")) {
                size += 3;
            }
            else if(datatype.equals("Date")){
                size += 11;
            }
        }

        return size;
    }

    private static int processJSONObject(JSONObject jsonObject) throws JSONException {
        int size = 0;

        for (Iterator it = jsonObject.keys(); it.hasNext(); ) {
            Object key = it.next();
            String fieldName = (String) key;
            Object fieldValue = jsonObject.get((String) key);
            String dataType = determineDataType(fieldValue);

            if(dataType.equals("String")) {
                String fieldVal = (String) jsonObject.get(fieldName);
                size += 7 + fieldName.length() + fieldVal.length();
            }
            else if(dataType.equals("Boolean")) {
                size += fieldName.length() + 3;
            }
            else if(dataType.equals("Number")) {
                size += fieldName.length() + 6;
            }
            else if(dataType.equals("Object")) {
                int objSize = processJSONObject((JSONObject) fieldValue);
                size += objSize + fieldName.length() + 7;
            }
            else if(dataType.equals("Array")) {
                int arraySize = processJSONArray((JSONArray) fieldValue);
                size += arraySize + fieldName.length() + 7;
            }
            else if(dataType.equals("null")) {
                size += fieldName.length() + 2;
            }
            else if(dataType.equals("Double")) {
                size += fieldName.length() + 10;
            }
            else if(dataType.equals("Date")){
                size += fieldName.length() + 10;
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
            size = -1;
        }
        return size;
    }

    public void findDocumentSize(String url) {
        int documentSize = findSize(url);
        if(documentSize == -1){
            System.out.println("Error reading the file");
        }
        else{
            documentSize += 5;
            System.out.println("Total document size : " + documentSize + " bytes");
        }
    }

    public void findOverhead(String url) {
        int overhead = findSize(url);
        if(overhead == -1){
            System.out.println("Error reading the file");
        }
        else{
            System.out.println("Overhead : " + overhead + " bytes");
        }
    }
}
