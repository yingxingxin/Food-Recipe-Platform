package org.example.foodrecipeplatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MealDbAPI {

    private static final String API_BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    // Search meals by name
    public List<CardData> searchMealsByName(String mealName) {
        String endpoint = API_BASE_URL + "search.php?s=" + mealName.replace(" ", "%20");
        return fetchMeals(endpoint);
    }

    // Get meals by first letter
    public List<CardData> getMealsByFirstLetter(char firstLetter) {
        String endpoint = API_BASE_URL + "search.php?f=" + firstLetter;
        return fetchMeals(endpoint);
    }

    // Gets a random meal
    public List<CardData> getRandomMeal() {
        String endpoint = API_BASE_URL + "random.php";
        return fetchMeals(endpoint);
    }

    // Gets meal by category
    public List<CardData> getMealsByCategory(String category) {
        String endpoint = API_BASE_URL + "filter.php?c=" + category.replace(" ", "%20");
        return fetchMealsFromFilter(endpoint);
    }

    // Gets meals by cuisine
    public List<CardData> getMealsByCuisine(String area) {
        String endpoint = API_BASE_URL + "filter.php?a=" + area.replace(" ", "%20");
        return fetchMealsFromFilter(endpoint);
    }

    // Gets meals by main ingredient
    public List<CardData> getMealsByIngredient(String ingredient) {
        String endpoint = API_BASE_URL + "filter.php?i=" + ingredient.replace(" ", "%20");
        return fetchMealsFromFilter(endpoint);
    }

 
    // Get detailed meal information by ID
    public List<CardData> getMealDetails(String mealId) {
        String endpoint = API_BASE_URL + "lookup.php?i=" + mealId;
        return fetchMealsFromFilter(endpoint);
    }

    // Get meals by country
    public List<CardData> getMealsByCountry(String Country) {
        String endpoint = API_BASE_URL + "filter.php?a=" + Country;

        return fetchMeals(endpoint);
    }

    // Gets meals by all categories
    public List<String> getAllCategories() {
        String endpoint = API_BASE_URL + "list.php?c=list";
        List<String> categories = new ArrayList<>();

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray categoriesArray = (JSONArray) json.get("meals");

            for (Object item : categoriesArray) {
                JSONObject category = (JSONObject) item;
                categories.add((String) category.get("strCategory"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Map<String, String> getMealIngredients(String mealId) {
        Map<String, String> ingredients = new HashMap<>();
        String endpoint = API_BASE_URL + "lookup.php?i=" + mealId;

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray mealsArray = (JSONArray) json.get("meals");

            if (mealsArray == null || mealsArray.isEmpty()) {
                return ingredients;
            }

            JSONObject meal = (JSONObject) mealsArray.get(0);

            for (int i = 1; i <= 20; i++) {
                String ingredient = (String) meal.get("strIngredient" + i);
                String measure = (String) meal.get("strMeasure" + i);

                if (ingredient != null && !ingredient.trim().isEmpty() && !ingredient.equals("null")) {
                    ingredients.put(ingredient.trim(), measure != null ? measure.trim() : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    // Helper method to fetch meals from search and lookup endpoints
    private List<CardData> fetchMeals(String endpoint) {
        List<CardData> meals = new ArrayList<>();

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray mealsArray = (JSONArray) json.get("meals");

            if (mealsArray == null) {
                return meals;
            }

            for (Object item : mealsArray) {
                JSONObject meal = (JSONObject) item;
                String mealId = (String) meal.get("idMeal");
                String mealName = (String) meal.get("strMeal");
                String mealThumb = (String) meal.get("strMealThumb");
                String mealInstructions = (String) meal.get("strInstructions");

                // Limit description length
                String description = mealInstructions != null ?
                        (mealInstructions.length() > 100 ? mealInstructions.substring(0, 97) + "..." : mealInstructions) :
                        "No description available"; // this is storing the instructions as decription, why?

                CardData cardData = new CardData(mealName, description, mealThumb);
                cardData.setMealId(mealId);
                meals.add(cardData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return meals;
    }

    // Helper method to fetch meals from filter endpoints
    private List<CardData> fetchMealsFromFilter(String endpoint) {
        List<CardData> meals = new ArrayList<>();

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray mealsArray = (JSONArray) json.get("meals");

            if (mealsArray == null) {
                return meals; // Return empty list if no meals found
            }

            for (Object item : mealsArray) {
                JSONObject meal = (JSONObject) item;
                String mealId = (String) meal.get("idMeal");
                String mealName = (String) meal.get("strMeal");
                String mealThumbnail = (String) meal.get("strMealThumb");

                CardData cardData = new CardData(mealName, "Click for details", mealThumbnail);
                cardData.setMealId(mealId);
                meals.add(cardData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return meals;
    }

    // Make HTTP request to API and return response as string
    private String makeApiRequest(String endpoint) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
