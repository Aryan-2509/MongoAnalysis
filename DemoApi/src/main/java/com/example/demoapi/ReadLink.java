package com.example.demoapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadLink {
    private static final Logger logger = Logger.getLogger(ReadLink.class.getName());

    private static String readURL(String urlString) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;

        try {
            URI uri = null;
            try {
                uri = new URI(urlString);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            URL url = uri.toURL();
            //URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return content.toString();
    }

    public JSONArray getJSONArray(String url) {
        JSONArray jsonArray = new JSONArray();

        try {
            String jsonData = readURL(url);
            jsonArray = new JSONArray(jsonData);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR: ", e);
            throw new RuntimeException(e);
        }

        return jsonArray;
    }

    public JSONObject getJSONObject(String inputUrl) {
        JSONObject jsonObject = null;

        try {
            URI uri = null;
            try {
                uri = new URI(inputUrl);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            URL url = uri.toURL();
            //URL url = new URL(inputUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            StringBuilder jsonString = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            Object data = new JSONTokener(jsonString.toString()).nextValue();

            if (data instanceof JSONObject) {
                jsonObject = (JSONObject) data;
            } else if (data instanceof JSONArray) {
                logger.log(Level.SEVERE, "ERROR: JSONArray is an invalid input");
                throw new RuntimeException("ERROR: JSONArray is an invalid input");
            }
            reader.close();
        } catch (IOException | JSONException e) {
            logger.log(Level.SEVERE, "An error occurred", e);
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

//    public JSONObject getJSONObject(String inputUrl) {
//        JSONObject jsonObject = null;
//
//        try {
//            String urlString = "https://example.com/api/data.json"; // Replace with your URL
//            URI uri = null;
//            try {
//                uri = new URI(urlString);
//            } catch (URISyntaxException e) {
//                throw new RuntimeException(e);
//            }
//
//            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//            connection.setRequestMethod("GET");
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                StringBuilder jsonContent = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    jsonContent.append(line);
//                }
//                reader.close();
//
//                // Parse the JSON content into a JSONObject
//                try {
//                    jsonObject = new JSONObject(jsonContent.toString());
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//            } else {
//                logger.log(Level.SEVERE, "An error occurred : " + responseCode);
//                throw new RuntimeException("ERROR: " + responseCode);
//            }
//
//            connection.disconnect();
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "An error occurred", e);
//            throw new RuntimeException(e);
//        }
//
//        return jsonObject;
//    }

}
