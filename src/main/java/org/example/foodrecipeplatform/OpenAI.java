package org.example.foodrecipeplatform;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAI {

    // Replace with your actual API key (consider moving to env var for safety)
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static String getFoodInfo(String foodName) {
        try {
            // Create connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON payload
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", "Give me nutritional facts and origin of the food item: " + foodName);

            JSONArray messagesArray = new JSONArray();
            messagesArray.put(message);

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("model", "gpt-3.5-turbo");
            jsonInput.put("messages", messagesArray);
            jsonInput.put("temperature", 0.7);

            // Send request
            OutputStream os = connection.getOutputStream();
            os.write(jsonInput.toString().getBytes());
            os.flush();
            os.close();

            // Get response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStrBuilder = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                responseStrBuilder.append(line);
            }

            br.close();

            // Parse response
            JSONObject jsonResponse = new JSONObject(responseStrBuilder.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObj = firstChoice.getJSONObject("message");

            return messageObj.getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, an error occurred while fetching food information.";
        }
    }
}
