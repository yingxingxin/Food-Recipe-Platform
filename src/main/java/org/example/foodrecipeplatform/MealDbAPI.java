package org.example.foodrecipeplatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * MealDbAPI -> class to access food db
 */
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

    public Map<String, String> getIngredientIdMap() {
        Map<String, String> ingredientIdMap = new HashMap<>();
        String endpoint = API_BASE_URL + "list.php?i=list";

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray ingredientsArray = (JSONArray) json.get("meals");

            for (Object item : ingredientsArray) {
                JSONObject ingredient = (JSONObject) item;
                String name = ((String) ingredient.get("strIngredient")).trim();
                String id = ((String) ingredient.get("idIngredient")).trim();
                ingredientIdMap.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ingredientIdMap;
    }

    // For picked recipe screen. Gets id, name, area, instructions, ingredients, & image. Stores results in a list
    public List<String> getAllDetails(String mealId) {
        String endpoint = API_BASE_URL + "lookup.php?i=" + mealId;

        List<String> results = new ArrayList<>();

        try {
            String jsonResponse = makeApiRequest(endpoint);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonResponse);
            JSONArray mealsArray = (JSONArray) json.get("meals");

            if (mealsArray == null) {
                return results;
            }

            for (Object item : mealsArray)
            {
                JSONObject meal = (JSONObject) item;
                String idMeal = (String) meal.get("idMeal");
                String mealName = (String) meal.get("strMeal");
                String mealThumb = (String) meal.get("strMealThumb");
                String mealInstructions = (String) meal.get("strInstructions");
                String area = (String) meal.get("strArea");
                String category = (String) meal.get("strCategory");

                // using StringBuilder because it's faster for concatenating
                StringBuilder strIngredient = new StringBuilder("strIngredient1");
                StringBuilder strMeasure = new StringBuilder("strMeasure1");

                String ingredient = (String) meal.get(strIngredient.toString()); // getting the first ingredient
                String measurements = (String) meal.get(strMeasure.toString()); // getting the first measurement

                // position is used to get all ingredients and measurements
                int position = 1;
                // ingredientsWithMeasurements is used to store the results
                StringBuilder ingredientsWithMeasurements = new StringBuilder();

                // used to loop through ingredients, if 1st ingredient is null, then don't loop
                boolean keepLooping = ingredient != null;
                while (keepLooping)
                {
                    //System.out.println(position + " " + ingredient + " " + measurements);
                    // adding ingredient & measurements
                    ingredientsWithMeasurements.append(ingredient)
                                               .append(" ")
                                               .append(measurements);

                    position++;

                    strIngredient.delete(13, strIngredient.length()); // deletes the number at the end, resulting in this: strIngredient
                    strIngredient.append(position); // add position to the end: strIngredient(position)

                    strMeasure.delete(10, strMeasure.length()); // deletes the number at the end, resulting in this: strMeasure
                    strMeasure.append(position); // add position to the end strMeasure(position)

                    //System.out.println(strIngredient + " " + position);

                    ingredient = (String) meal.get(strIngredient.toString());
                    measurements = (String) meal.get(strMeasure.toString());

                    // exits loop when there is no ingredients left
                    if (ingredient == null || ingredient.equals(""))
                        keepLooping = false;
                    else
                        ingredientsWithMeasurements.append("\n");
                }

                //System.out.println(idMeal + "\n" + mealName + "\n" + mealThumb + "\n" + mealInstructions + "\n" + area);
                //System.out.println(ingredientsWithMeasurements);

                //adding all the list
                Collections.addAll(results, idMeal, mealName, mealThumb, mealInstructions, area, ingredientsWithMeasurements.toString(), category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
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
                String mealThumb = (String) meal.get("strMealThumb");

                CardData cardData = new CardData(mealName, "Click for details", mealThumb);
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
} // End MealDbAPI class
