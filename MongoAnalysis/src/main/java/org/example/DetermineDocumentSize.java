package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;

public class DetermineDocumentSize {
    private static String determineDataType(Object fieldValue) {
        if (fieldValue instanceof String) {
            return "String";
        }
        else if (fieldValue instanceof Boolean) {
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
        else {
            return "null";
        }
    }

    private static int processJSONArray(JSONArray jsonArray) {

        int size = 0;

        for(Object arrayElement : jsonArray)
        {
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
        }

        return size;
    }

    private static int processJSONObject(JSONObject jsonObject)
    {
        int size = 0;

        for (Object key : jsonObject.keySet())
        {
            String fieldName = (String) key;
            Object fieldValue = jsonObject.get(key);
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
        }

        return size;
    }

    private static int findSize(String jsonFilePath){
        JSONParser parser = new JSONParser();
        int size;

        try (FileReader fileReader = new FileReader(jsonFilePath)) {
            Object obj = parser.parse(fileReader);
            JSONObject jsonObject = (JSONObject) obj;
            size= processJSONObject(jsonObject);
            return size;
        }
        catch (Exception e) {
            return -1;
        }
    }
    public static void findDocumentSize(String jsonFilePath) {

        int documentSize = findSize(jsonFilePath);
        if(documentSize == -1){
            System.out.println("Error reading the file");
        }
        else{
            System.out.println("Total document size : " + documentSize + " bytes");
        }
    }

    public static void findOverhead(String jsonFilePath){
        int overhead = findSize(jsonFilePath);
        if(overhead == -1){
            System.out.println("Error reading the file");
        }
        else{
            System.out.println("Total document size : " + overhead + " bytes");
        }
    }
}
