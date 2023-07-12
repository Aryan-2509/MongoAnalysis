package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetermineDocumentSizeMapper {

    private static HashMap<String, Integer> hashMap = new HashMap<>();

    static {
        hashMap.put("BOOLEAN",3);
        hashMap.put("INTEGER",6);
        hashMap.put("NULL",2);
        hashMap.put("DOUBLE",10);
        hashMap.put("DATE",10);
        hashMap.put("Array",10);
        hashMap.put("List",10);
        hashMap.put("ArrayList",10);
    }
    private static final Logger logger = Logger.getLogger(DetermineDocumentSizeMapper.class.getName());

    private static int processClass(Class<?> LoadedClass){

        Class<?> targetClass = LoadedClass;
        int size = 0;

        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String dataType = fieldType.getSimpleName();
            System.out.println("Field: " + fieldName + ", Data Type: " + dataType);

            switch (dataType) {
                case "String":
                    int averageStringLength = 10;
                    size += 7 + fieldName.length() + averageStringLength;
                    break;
                case "boolean":
                    size += fieldName.length() + 3;
                    break;
                case "int":
                    size += fieldName.length() + 6;
                    break;
                case "Array":
                    size += 10;
//                    int arraySize = processJSONArray((JSONArray) fieldValue);
//                    size += arraySize + fieldName.length() + 7;
                    break;
                case "ArrayList":
                    size += 10;
                    break;
                case "null":
                    size += fieldName.length() + 2;
                    break;
                case "double":
                    size += fieldName.length() + 10;
                    break;
                case "Date":
                    size += fieldName.length() + 10;
                    break;
                case "List":
                    size += 10;
                    break;
                default:{

                    String className = dataType;
                    String classNamePath = "org.example." + className;

                    try {
                        Class<?> loadedClass = Class.forName(classNamePath);
                        //Class<?> loadedClass = Class.forName(className, true, getAppClassLoader());

                        int inputSize = processClass(loadedClass);
                        size += inputSize + 7;

                        System.out.println("Loaded class: " + loadedClass.getName());

                    } catch (ClassNotFoundException e) {
                        System.out.println("Failed to load class: " + className);
                        logger.log(Level.SEVERE, "ERROR: Failed to load Class");
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return size;
    }

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
//                case "Object":
//                    JSONObject jsonObject = (JSONObject) arrayElement;
//                    int objSize = processJSONObject(jsonObject) + 7 + 1;
//                    size += objSize;
//                    break;
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

        if (jsonObject.has("fieldName")){

            String fieldName = jsonObject.getString("fieldName");
            size += fieldName.length();

            if(jsonObject.has("isList")){

                System.out.println("isList found");
                boolean isList = jsonObject.getBoolean("isList");

                if(isList){
                    if(jsonObject.has("list")){
                        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                        int ListSize = processJSONArray(jsonArray);
                        size += ListSize + 7;
                    }
                    else{
                        logger.log(Level.SEVERE, "ERROR: List absent from input");
                        throw new RuntimeException("ERROR: List absent from input");
                    }
                    return size;
                }
            }

            boolean classRequired = false;

            if (jsonObject.has("fieldType")) {

                String fieldType = jsonObject.getString("fieldType");
                String fieldTypeCapital = fieldType.toUpperCase();

                if(hashMap.containsKey(fieldTypeCapital)){
                    size += hashMap.get(fieldTypeCapital);
                }
                else if(fieldTypeCapital.equals("STRING")){
                    if(jsonObject.has("inputString")){
                        String inputString = jsonObject.getString("inputString");
                        size += inputString.length() + 7;
                    }
                }
                else{
                    classRequired = true;
                }

                System.out.println("fieldType is given: " + fieldType);

            }
            else{
                classRequired = true;
            }

            if(classRequired){

                if(jsonObject.has("class")){

                    String className = jsonObject.getString("class");
                    System.out.println("class is given: " + className);
                    String classNamePath = "org.example." + className;

                    try {
                        Class<?> loadedClass = Class.forName(classNamePath);
                        //Class<?> loadedClass = Class.forName(className, true, getAppClassLoader());

                        int inputSize = processClass(loadedClass);
                        size += inputSize + 7;

                        System.out.println("Loaded class: " + loadedClass.getName());

                    } catch (ClassNotFoundException e) {
                        System.out.println("Failed to load class: " + className);
                        logger.log(Level.SEVERE, "ERROR: Failed to load Class");
                        throw new RuntimeException(e);
                    }
                }
                else{
                    logger.log(Level.SEVERE, "ERROR: Class not provided");
                    throw new RuntimeException("ERROR: Class not provided");
                }
            }
        }
        else{
            logger.log(Level.SEVERE, "ERROR: FieldName not found");
            throw new RuntimeException("ERROR: FieldName not found");
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
            throw new RuntimeException(e);
        }
        return size;
    }

//    public int findDocumentSize(String url) {
//        return findSize(url) + 5;
//    }

    public int findDocumentSize(String classPath){
        int size = 0;

        try {
            Class<?> loadedClass = Class.forName(classPath);
            //Class<?> loadedClass = Class.forName(className, true, getAppClassLoader());

            int inputSize = processClass(loadedClass);
            size += inputSize + 5;

            System.out.println("Loaded class: " + loadedClass.getName());

        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load class: " + classPath);
            logger.log(Level.SEVERE, "ERROR: Failed to load Class");
            throw new RuntimeException(e);
        }

        return size;
    }
    public int findOverhead(String url) {
        return findSize(url);
    }
}
