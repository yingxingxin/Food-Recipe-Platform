package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
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

/**
 * PickedRecipeScreenController -> class that shows the recipe card with all of its details
 */
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
        getImage_DB();
        instructionsTextArea.setWrapText(true);
        ingredientTextArea.setWrapText(true);
        shoppingList = new ShoppingList();
    } // End initialize

    /**
     * setCurrentMealId -> sets meal id
     * @param mealId
     */
    public void setCurrentMealId(String mealId) {
        this.currentMealId = mealId;

                Executors.newSingleThreadExecutor().submit(() -> {
            try {
                checkIfFavoritedAndUpdateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    } // End setCurrentMealId method

    /**
     * getCurrentMealId -> gets meal id
     * @param mealId
     */
    public void getCurrentMealId(String mealId) {
        this.currentMealId = mealId;
    } // End getCurrentMealId method

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException {
        // switch screen
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    } // End backButtonClicked method

    @FXML
    void favoriteButtonClicked(ActionEvent event) throws IOException, ExecutionException, InterruptedException {
        System.out.println("favorite button clicked");
        toggleFavorite();
    } // End favoriteButtonClicked method



    // Called on screen upon load when meal id is set to reflect correct UI state of favorite button
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
    } // End checkIfFavoritedAndUpdateUI End


    // Called when user clicks the favorite button changes the db/ui state
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
            // Glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setRadius(20);
            favoriteButton.setEffect(glow);

            // Remove glow after 1 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> favoriteButton.setEffect(null));
            pause.play();

            // Pop animation
            ScaleTransition st = new ScaleTransition(Duration.millis(200), favoriteButton);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.2);
            st.setToY(1.2);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();

        } else {
            favoriteButton.setText("Favorite ⭐");
        }


        System.out.println("is Meal Favorited: " + newFavoriteValue);
    } // End toggleFavorite method


    /**
     * shoppinglistButtonClicked -> method that adds the meal ingredients into list
     * @param event
     * @throws IOException
     */
    @FXML
    void shoppinglistButtonClicked(ActionEvent event) throws IOException
    {
        System.out.println("Add to shopping list button clicked");

        if (currentMealId != null && !currentMealId.isEmpty()) {

            shoppingList.addIngredients(currentMealId);

            shoppinglistButton.setText("Added to Shopping List ✓");
            // Pop animation
            ScaleTransition st = new ScaleTransition(Duration.millis(200), shoppinglistButton);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.2);
            st.setToY(1.2);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
            shoppinglistButton.setDisable(true); // 🔒 Disable the button

        } else {

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() ->
                            shoppinglistButton.setText("Add to Shopping List"));
                            shoppinglistButton.setDisable(false); // 🔓 Re-enable the button
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            System.out.println("No meal ID available to add to shopping list");

        }
    } // End shoppinglistButtonClicked method

    @FXML
    void profileHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("profile hyperlink clicked");
        FoodRecipePlatform.setRoot("ProfilePage");
    } // End profileHyperLinkClicked method

    @FXML
    void shoppingHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("shopping hyperlink clicked");
        FoodRecipePlatform.setRoot("ShoppingScreen");
    } // End shoppingHyperLinkClicked method

    @FXML
    void homeHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("home hyperlink clicked");
        FoodRecipePlatform.setRoot("HomeScreen");
    } // End homeHyperLinkClicked method

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
    } // End setRecipeName method

    public void setRecipeImage(Image recipeImage)
    {
        recipeImageView.setImage(recipeImage);
    } // End setRecipeImage method

    public void setArea(String area)
    {
        areaText.setText(area);
    } // End setArea method

    public void setIngredientTextArea(String ingredient)
    {
        ingredientTextArea.setText(ingredient);
    } // End setIngredientTextArea method

    public void setInstructionsTextArea(String instructions)
    {
        instructionsTextArea.setText(instructions);
    } // End setInstructionsTextArea method

    public void setCategoryText(String category)
    {
        categoryText.setText(category);
    } // End setCategoryText method

} // End PickedRecipeScreenController class