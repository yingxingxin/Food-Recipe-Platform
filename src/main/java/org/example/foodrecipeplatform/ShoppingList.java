package org.example.foodrecipeplatform;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.foodrecipeplatform.Model.ShoppingItem;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ShoppingList {
    private static ShoppingList instance;
    private final Map<String, ShoppingItem> shoppingList = new HashMap<>();

    public ShoppingList() {

    }

    // Adds recipe's ingredients to the shopping list
    public void addIngredients(String mealId) {
        // To be implemented: add ingredients
    }

    // Removes ingredient from shopping list
    public void removeIngredient(String ingredientName) {
        shoppingList.remove(ingredientName);
        //saveShoppingListToFirebase();
    }

    // Clears all ingredients from shopping list
    public void clearShoppingList() {
        // To be implemented: method to clear the shopping list
    }

    // Sets ingredient status to checked
    public void setIngredientChecked(String ingredientName, boolean checked) {
        // To be implemented: method that sets ingredient status to checked
    }

    // Updates status of shopping ingredients when checked (saving to Firebase)
    public void saveShoppingListToFirebase(String ingredientName, boolean checked) {
        // To be implemented: method to save current shopping list to Firebase
    }

    // Gets current shopping list
    //public List<ShoppingItem> getShoppingList() {
    //    To be implemented: method to load current shopping list
    //}



}