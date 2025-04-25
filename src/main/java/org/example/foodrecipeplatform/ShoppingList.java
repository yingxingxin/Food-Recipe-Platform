package org.example.foodrecipeplatform;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.foodrecipeplatform.Controller.SessionManager;
import org.example.foodrecipeplatform.Model.ShoppingItem;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ShoppingList {
    private static ShoppingList instance;
    private final Map<String, ShoppingItem> shoppingList = new HashMap<>();
    private final MealDbAPI mealDbAPI = new MealDbAPI();

    public ShoppingList() {

    }

    // Adds recipe's ingredients to the shopping list
    public void addIngredients(String mealId) {
        Map<String, String> ingredientsMap = mealDbAPI.getMealIngredients(mealId);
        Map<String, String> ingredientIdMap = mealDbAPI.getIngredientIdMap();

        for (Map.Entry<String, String> entry : ingredientsMap.entrySet()) {
            String ingredient = entry.getKey();
            String quantity = entry.getValue();
            String idIngredient = ingredientIdMap.getOrDefault(ingredient, ingredient);

            if (!shoppingList.containsKey(idIngredient)) {
                ShoppingItem item = new ShoppingItem(ingredient, quantity);
                shoppingList.put(idIngredient, item);
                saveShoppingItemToFirebase(idIngredient, item);
            } else {
                System.out.print("Error when adding idIngredients together.");
                // Optionally, aggregate quantities here if needed
            }
        }
        System.out.println("Added ingredients to shopping list: " + shoppingList.keySet());
    }

//    // Removes ingredient from shopping list
//    public void removeIngredient(String ingredientName) {
//        shoppingList.remove(ingredientName);
//        //saveShoppingListToFirebase();
//    }

//    // Clears all ingredients from shopping list
//    public void clearShoppingList() {
//        // To be implemented: method to clear the shopping list
//    }

//    // Sets ingredient status to checked
//    public void setIngredientChecked(String ingredientName, boolean checked) {
//        // To be implemented: method that sets ingredient status to checked
//    }

    private void saveShoppingItemToFirebase(String idIngredient, ShoppingItem item) {

        String userId = SessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            System.out.println("No user is signed in. Cannot save shopping item.");
            return;
        }

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("ingredientName", item.getIngredientName());
            data.put("quantity", item.getQuantity());
            data.put("checked", item.isChecked());
            data.put("idIngredient", idIngredient);

            DocumentReference docRef = FirestoreClient.getFirestore()
                    .collection("Users")
                    .document(userId)
                    .collection("ShoppingList")
                    .document(idIngredient);

            ApiFuture<WriteResult> result = docRef.set(data);
            WriteResult writeResult = result.get();
            System.out.println("Saved to Firestore: " + item.getIngredientName());

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to save shopping item: " + item.getIngredientName());
            e.printStackTrace();
        }
    }

//    // Updates status of shopping ingredients when checked (saving to Firebase)
//    public void saveShoppingListToFirebase(String ingredientName, boolean checked) {
//        // To be implemented: method to save current shopping list to Firebase
//    }

    // Gets current shopping list
    //public List<ShoppingItem> getShoppingList() {
    //    To be implemented: method to load current shopping list
    //}



}