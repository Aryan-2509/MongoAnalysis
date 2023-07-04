package org.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ReadLink {
    public JSONArray getJSONArray(String url) {
        JSONArray jsonArray = new JSONArray();
        try {
            URL urlObj = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlObj.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject jsonObject = new JSONObject(line);
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
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
