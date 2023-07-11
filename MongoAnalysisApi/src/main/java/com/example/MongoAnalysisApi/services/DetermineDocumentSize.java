package com.example.MongoAnalysisApi.services;

import com.example.MongoAnalysisApi.ReadLink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DetermineDocumentSize implements DetermineDocumentSizeService{

    private static HashMap<String, Integer> primitiveDataTypes = new HashMap<>();

    static {
        primitiveDataTypes.put("BOOLEAN",3);
        primitiveDataTypes.put("INTEGER",6);
        primitiveDataTypes.put("NULL",2);
        primitiveDataTypes.put("DOUBLE",10);
        primitiveDataTypes.put("DATE",10);
        primitiveDataTypes.put("ARRAY",10);
        primitiveDataTypes.put("LIST",10);
        primitiveDataTypes.put("ARRAYLIST",10);
    }
    private static final Logger logger = Logger.getLogger(DetermineDocumentSize.class.getName());

    private static int processClass(Class<?> LoadedClass){
        Class<?> targetClass = LoadedClass;
        int size = 0;

        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String dataType = fieldType.getSimpleName();

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
                    String classNamePath = "com.example.MongoAnalysisApi." + className;

                    try {
                        Class<?> loadedClass = Class.forName(classNamePath);
                        int inputSize = processClass(loadedClass);
                        size += inputSize + 7;
                    } catch (ClassNotFoundException e) {
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
                case "Object":
                    size += 20;
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

        if (jsonObject.has("fieldName")) {
            String fieldName = jsonObject.getString("fieldName");
            size += fieldName.length();

            if (jsonObject.has("isList")) {
                boolean isList = jsonObject.getBoolean("isList");
                if (isList) {
                    if (jsonObject.has("list")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        int listSize = processJSONArray(jsonArray);
                        size += listSize + 7;
                    } else {
                        throw new RuntimeException("ERROR: List absent from input");
                    }
                    return size;
                }
            }

            boolean classRequired = false;

            if (jsonObject.has("fieldType")) {
                String fieldType = jsonObject.getString("fieldType");
                String fieldTypeCapital = fieldType.toUpperCase();

                if (primitiveDataTypes.containsKey(fieldTypeCapital)) {
                    size += primitiveDataTypes.get(fieldTypeCapital);
                } else if (fieldTypeCapital.equals("ARRAY") || fieldTypeCapital.equals("ARRAYLIST")) {
                    throw new RuntimeException("ERROR: isList marked false");
                } else if (fieldTypeCapital.equals("STRING")) {
                    if (jsonObject.has("inputString")) {
                        String inputString = jsonObject.getString("inputString");
                        size += inputString.length() + 7;
                    }
                } else {
                    classRequired = true;
                }
            } else {
                classRequired = true;
            }

            if (classRequired) {
                if (jsonObject.has("class")) {
                    String classNamePath = jsonObject.getString("class");
                    String className = classNamePath;

                    try {
                        Class<?> loadedClass = Class.forName(classNamePath);
                        int inputSize = processClass(loadedClass);
                        size += inputSize + 7;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("ERROR: Class not found", e);
                    }
                } else {
                    throw new RuntimeException("ERROR: Class not provided");
                }
            }
        } else {
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

    @Override
    public int findDocumentSize(String classPath){
        int size = 0;

        try {
            Class<?> loadedClass = Class.forName(classPath);
            int inputSize = processClass(loadedClass);
            size += inputSize + 5;
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "ERROR: Failed to load Class");
            throw new RuntimeException(e);
        }

        return size;
    }

    @Override
    public int findOverhead(String url) {
        return findSize(url);
    }
}

