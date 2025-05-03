package org.example.foodrecipeplatform;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.foodrecipeplatform.Controller.SessionManager;
import org.example.foodrecipeplatform.Model.ShoppingItem;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * ShoppingList -> Class to add food cards recipes items -> to personalized list
 */
public class ShoppingList {
    private final Map<String, ShoppingItem> shoppingList = new HashMap<>();
    private final MealDbAPI mealDbAPI = new MealDbAPI();

    public ShoppingList() {
        loadExistingItems();
    }

    // Loads existing items from Firebase
    private void loadExistingItems() {
        String userId = SessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        try {
            CollectionReference shoppingListRef = FirestoreClient.getFirestore()
                    .collection("Users")
                    .document(userId)
                    .collection("ShoppingList");

            ApiFuture<QuerySnapshot> future = shoppingListRef.get();
            QuerySnapshot snapshot = future.get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                String ingredientName = doc.getString("ingredientName");
                String quantity = doc.getString("quantity");
                Boolean checked = doc.getBoolean("checked");
                String idIngredient = doc.getString("idIngredient");

                if (idIngredient == null) {
                    idIngredient = ingredientName;
                }

                ShoppingItem item = new ShoppingItem(ingredientName, quantity);
                if (checked != null) {
                    item.setChecked(checked);
                }

                shoppingList.put(idIngredient, item);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Failed to load existing shopping items");
            e.printStackTrace();
        }
    }

    // Adds recipe's ingredients to the shopping list and saves to Firestore
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
                System.out.print("Ingredient already exists.");
                // Will aggregate quantities next
            }
        }
        System.out.println("Added ingredients to shopping list: " + shoppingList.keySet());
    }

    // saves shopping item to Firebase
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


    @FXML
    void back_to_home_Button(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("HomeScreen");
    } // End back_to_home_Button

} // End ShoppingList class