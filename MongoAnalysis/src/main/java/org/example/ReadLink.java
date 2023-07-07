package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadLink {
    private static final Logger logger = Logger.getLogger(DetermineDocumentSize.class.getName());
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

    private static String readURL(String urlString) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
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

    public JSONObject getJSONObject(String inputUrl) {
        JSONObject jsonObject = null;

        try {
            URL url = new URL(inputUrl);
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
}
