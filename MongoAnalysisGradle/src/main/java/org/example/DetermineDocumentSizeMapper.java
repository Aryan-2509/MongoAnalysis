package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.logging.Logger;

public class DetermineDocumentSizeMapper {
    private static final Logger logger = Logger.getLogger(DetermineDocumentSizeMapper.class.getName());
    private static int processJSONObject(JSONObject jsonObject){
        ObjectMapper objectMapper = new ObjectMapper();
        Book book;
        int size = 0;

        try {
            book = objectMapper.readValue(jsonObject.toString(), Book.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Field[] fields = book.getClass().getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String dataType = fieldType.getSimpleName();
            Object value;

            try {
                value = field.get(book);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if(value == null){
                size += fieldName.length() + 2;
                continue;
            }

            Object fieldValue = null;
            try {
                field.setAccessible(true);
                fieldValue = field.get(book);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

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
//                    int objSize = processJSONObject((JSONObject) fieldValue);
//                    size += objSize + fieldName.length() + 7;
                    break;
                case "Array":
//                    int arraySize = processJSONArray((JSONArray) fieldValue);
//                    size += arraySize + fieldName.length() + 7;
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
        size = processJSONObject(jsonObject);
        return size;
    }

    public int findDocumentSize(String url) {
        return findSize(url) + 5;
    }

    public int findOverhead(String url) {
        return findSize(url);
    }
}
