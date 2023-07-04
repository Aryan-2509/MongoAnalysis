package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ReadLink {

    public JSONArray getJSONArray(String url){

        JSONArray jsonArray = new JSONArray();
        try {
            String jsonData = readURL(url);
            System.out.println("URL READ");
            jsonArray = new JSONArray(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(jsonArray.length() + " documents read");
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

        JSONObject jsonObject = new JSONObject();
        try {
            URL url = new URL(inputUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            StringBuilder jsonString = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            jsonObject = new JSONObject(jsonString.toString());
            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
