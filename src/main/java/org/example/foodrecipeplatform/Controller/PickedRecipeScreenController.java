package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;
import org.example.foodrecipeplatform.Model.ShoppingItem;
import org.example.foodrecipeplatform.ShoppingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class PickedRecipeScreenController {
    @FXML
    public Text nameText;
    @FXML
    public Text categoryText;
    @FXML
    public Text areaText;
    @FXML
    public ImageView recipeImageView;
    @FXML
    public TextArea ingredientTextArea;
    @FXML
    public TextArea instructionsTextArea;
    @FXML
    public Button backButton;
    @FXML
    public Button favoriteButton;
    @FXML
    public Button shoppinglistButton;
    @FXML
    public Hyperlink profileHyperLink;
    @FXML
    public Hyperlink shoppingHyperLink;
    @FXML
    public Hyperlink homeHyperLink;
    @FXML
    private ImageView UserProfilePhoto;

    private String profilePictureUrl;
    private String currentMealId;
    private ShoppingList shoppingList;
    MealDbAPI api;

    /**
     * Helper Method to call Alert
     * @param title   -> title of alert
     * @param header  -> header of alert
     * @param content -> content body of alert
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    } // End showAlert method

    @FXML
    void initialize() throws ExecutionException, InterruptedException {
        api = new MealDbAPI();
        // method to get profile picture from database
        getImage_DB();
        //check_if_favorite();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                checkIfFavoritedAndUpdateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        instructionsTextArea.setWrapText(true);
        ingredientTextArea.setWrapText(true);
        // make the back button save the results on the screen?

        shoppingList = new ShoppingList();
    }

    public void setCurrentMealId(String mealId) {
        this.currentMealId = mealId;
    }

    public void getCurrentMealId(String mealId) {
        this.currentMealId = mealId;
    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    }

//    private ArrayList<String> FavoritedItemsList;

    @FXML
    void favoriteButtonClicked(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        System.out.println("favorite button clicked");
        toggleFavorite();

        //check_if_favorite();

    } // End favoriteButtonClicked method



    // Called on screen load to reflect correct UI state
    public void checkIfFavoritedAndUpdateUI() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        if (currentMealId == null || currentMealId.isEmpty()) {
            System.out.println("Meal ID is empty");
            return;
        }

        DocumentReference mealDocRef = FoodRecipePlatform.fstore
                .collection("Users")
                .document(userId)
                .collection("favoritedFoods")
                .document(currentMealId);

        DocumentSnapshot document = mealDocRef.get().get();

        boolean isFavorited = false;
        if (document.exists()) {
            Boolean storedValue = document.getBoolean("favorite");
            isFavorited = storedValue != null && storedValue;
        }

        final boolean finalIsFavorited = isFavorited;
        Platform.runLater(() -> {
            if (finalIsFavorited) {
                favoriteButton.setText("Favorited \uD83C\uDF1F");
            } else {
                favoriteButton.setText("Favorite ⭐");
            }
        });
    }


    // Called when user clicks the favorite button
    public void toggleFavorite() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        if (currentMealId == null || currentMealId.isEmpty()) {
            System.out.println("Meal ID is empty");
            return;
        }

        CollectionReference favoritedFoodListRef = FoodRecipePlatform.fstore
                .collection("Users")
                .document(userId)
                .collection("favoritedFoods");

        DocumentReference mealDocRef = favoritedFoodListRef.document(currentMealId);

        ApiFuture<DocumentSnapshot> future = mealDocRef.get();
        DocumentSnapshot document = future.get();

        boolean isCurrentlyFavorited = false;

        if (document.exists()) {
            Boolean storedValue = document.getBoolean("favorite");
            isCurrentlyFavorited = storedValue != null && storedValue;
        }

        boolean newFavoriteValue = !isCurrentlyFavorited;

        Map<String, Object> favoriteMap = new HashMap<>();
        favoriteMap.put("FavoritedID", currentMealId);
        favoriteMap.put("favorite", newFavoriteValue);

        favoritedFoodListRef.document(currentMealId).set(favoriteMap)
                .addListener(() -> System.out.println("Meal Favorited: " + currentMealId), Runnable::run);

        if (newFavoriteValue) {
            favoriteButton.setText("Favorited \uD83C\uDF1F");
        } else {
            favoriteButton.setText("Favorite ⭐");
        }

        System.out.println("is Meal Favorited: " + newFavoriteValue);
    }





//    public void check_if_favorite() throws ExecutionException, InterruptedException {
//        String userId = SessionManager.getUserId();
//
//        if (currentMealId == null || currentMealId.isEmpty()) {
//            System.out.println(" Meal ID is empty");
//            return;
//        }
//
//        CollectionReference favoritedFoodListRef = FoodRecipePlatform.fstore
//                .collection("Users")
//                .document(userId)
//                .collection("favoritedFoods");
//
//
//        DocumentReference mealDocRef = FoodRecipePlatform.fstore
//                .collection("Users")
//                .document(userId)
//                .collection("favoritedFoods")
//                .document(currentMealId);
//
//        // Get current value of document id
//        ApiFuture<DocumentSnapshot> future = mealDocRef.get();
//        DocumentSnapshot document = future.get();
//
//        boolean isCurrentlyFavorited = false;
//
//        if (document.exists()) {
//            Boolean storedValue = document.getBoolean("favorite");
//            isCurrentlyFavorited = storedValue != null && storedValue;
//        }
//
//        // Toggle favorite value
//        boolean newFavoriteValue = !isCurrentlyFavorited;
//
//        // Save Data
//        Map<String, Object> favoriteMap = new HashMap<>();
//        favoriteMap.put("FavoritedID", currentMealId);
//        favoriteMap.put("favorite", newFavoriteValue);
//
//        favoritedFoodListRef.document(currentMealId).set(favoriteMap)
//                .addListener(() ->
//                        System.out.println("Meal Favorited: " + currentMealId), Runnable::run
//                );
//
//        System.out.println("is Meal Favorited: " + newFavoriteValue);
//
//        // Update button style
//        if (newFavoriteValue) {
//            //favoriteButton.setStyle("-fx-background-color: yellow;");
//            favoriteButton.setText("Favorited \uD83C\uDF1F");
//        } else {
//            favoriteButton.setText("Favorite ⭐");
//            favoriteButton.setStyle(""); // reset to default
//        }
//    }





    @FXML
    void shoppinglistButtonClicked(ActionEvent event) throws IOException
    {
        System.out.println("Add to shopping list button clicked");

        if (currentMealId != null && !currentMealId.isEmpty()) {

            shoppingList.addIngredients(currentMealId);

            shoppinglistButton.setText("Added to Shopping List ✓");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() ->
                            shoppinglistButton.setText("Add to Shopping List"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            System.out.println("No meal ID available to add to shopping list");
        }
    }

    @FXML
    void profileHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("profile hyperlink clicked");
        FoodRecipePlatform.setRoot("ProfilePage");
    }

    @FXML
    void shoppingHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("shopping hyperlink clicked");
        FoodRecipePlatform.setRoot("ShoppingScreen");
    }

    @FXML
    void homeHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("home hyperlink clicked");
        FoodRecipePlatform.setRoot("HomeScreen");
    }

    /**
     * getImage_DB ->  method to get profile pic from db -> image is set on profile page screen
     */
    void getImage_DB() {
        String userId = SessionManager.getUserId();
        System.out.println("User ID in getImage_DB: " + userId); // Debug

        profilePictureUrl = null;

        try {
            DocumentReference userDocRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId);

            ApiFuture<DocumentSnapshot> future = userDocRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                String photoUrl = document.getString("ProfilePicture");
                System.out.println("Retrieved photoUrl from Firebase: " + photoUrl); // Debug

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    profilePictureUrl = photoUrl;
                    try {
                        Image image = new Image(profilePictureUrl);
                        UserProfilePhoto.setImage(image);
                        System.out.println("Image set successfully.");
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error creating Image: " + e.getMessage());
                        showAlert("Error", "Invalid Profile Picture URL", "The URL retrieved is not valid.");
                    }
                } else {
                    showAlert("Warning", "Profile Picture URL is empty or missing.", null);
                }
            } else {
                showAlert("Error", "User document not found.", null);
            }
        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Failed to load profile picture URL from database.", e.getMessage());
            e.printStackTrace();
        }
    } // End getImage_DB method


    public void setRecipeName(String recipeName)
    {
        nameText.setText(recipeName);
    }

    public void setRecipeImage(Image recipeImage)
    {
        recipeImageView.setImage(recipeImage);
    }

    public void setArea(String area)
    {
        areaText.setText(area);
    }

    public void setIngredientTextArea(String ingredient)
    {
        ingredientTextArea.setText(ingredient);
    }

    public void setInstructionsTextArea(String instructions)
    {
        instructionsTextArea.setText(instructions);
    }

    public void setCategoryText(String category)
    {
        categoryText.setText(category);
    }
}



//        String userId = SessionManager.getUserId();
//
//        if (currentMealId == null || currentMealId.isEmpty()) {
//            System.out.println(" Meal ID is empty");
//            return;
//        }
//
//        CollectionReference favoritedFoodListRef = FoodRecipePlatform.fstore
//                .collection("Users")
//                .document(userId)
//                .collection("favoritedFoods");
//
//
//        DocumentReference mealDocRef = FoodRecipePlatform.fstore
//                .collection("Users")
//                .document(userId)
//                .collection("favoritedFoods")
//                .document(currentMealId);
//
//        // Get current value of document id
//        ApiFuture<DocumentSnapshot> future = mealDocRef.get();
//        DocumentSnapshot document = future.get();
//
//        boolean isCurrentlyFavorited = false;
//
//        if (document.exists()) {
//            Boolean storedValue = document.getBoolean("favorite");
//            isCurrentlyFavorited = storedValue != null && storedValue;
//        }
//
//        // Toggle favorite value
//        boolean newFavoriteValue = !isCurrentlyFavorited;
//
//        // Save Data
//        Map<String, Object> favoriteMap = new HashMap<>();
//        favoriteMap.put("FavoritedID", currentMealId);
//        favoriteMap.put("favorite", newFavoriteValue);
//
//        favoritedFoodListRef.document(currentMealId).set(favoriteMap)
//                .addListener(() ->
//                        System.out.println("Meal Favorited: " + currentMealId), Runnable::run
//                );
//
//        // Update button style
//        if (newFavoriteValue) {
//            //favoriteButton.setStyle("-fx-background-color: yellow;");
//            favoriteButton.setText("Favorited \uD83C\uDF1F");
//        } else {
//            favoriteButton.setText("Favorite ⭐");
//            favoriteButton.setStyle(""); // reset to default
//        }