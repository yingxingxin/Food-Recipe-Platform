package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;
import org.example.foodrecipeplatform.Model.ShoppingItem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * HomeScreenController -> class to display Home_screen screen
 */
public class HomeScreenController implements Initializable {

    // Methods to switch screens !
    @FXML
    void OpenFoodGeneratorScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    } // End OpenFoodGeneratorScreen
    @FXML
    void OpenProfileScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("ProfilePage");
    } // End OpenProfileScreen
    @FXML
    void OpenShoppingListScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("ShoppingScreen");
    } // End OpenShoppingListScreen

    @FXML
    private ScrollPane scroll;
    @FXML
    private GridPane grid;
    @FXML
    private ImageView homeScreen_pfp;


    List<CardData> cards = new ArrayList<>();             // cards lists -> array
    MealDbAPI api;                                        // Meal DB API
    private String profilePictureUrl;                     // To store the retrieved URL

    /**
     * Helper Method to call Alert
     * @param title -> title of alert
     * @param header -> header of alert
     * @param content -> content body of alert
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    } // End showAlert method

    /**
     * setGrid => Method to set up the grid and insert cards into grid
     * @param inputCardList -> takes in a list of cards to display onto grid
     */
    private void setGrid(List<CardData> inputCardList) {
        // Method to Display Card data on grin in scroll pane
        grid.getChildren().clear();
        cards = inputCardList;

        int row = 1;
        int col = 0;

        try {
            for (int i = 0; i < cards.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL fxmlPath = getClass().getResource("/org/example/foodrecipeplatform/RecipeCard.fxml");
                // System.out.println("FXML path = " + fxmlPath); // Debug

                if (fxmlPath == null) {
                    System.out.println("Could not find RecipeCard.fxml");
                    continue;
                }

                fxmlLoader.setLocation(fxmlPath);
                AnchorPane anchorPane = fxmlLoader.load();

                RecipeCardController recipeCardController = fxmlLoader.getController();
                recipeCardController.setData(cards.get(i));

                if (col == 3) {
                    col = 0;
                    row++;
                }
                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            System.out.println("Failed to load RecipeCard.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("General error in initialize");
            e.printStackTrace();
        }
    } // End setGrid method


    /**
     * initialize -> method for init
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // method to get profile photo from database
        getImage_DB();

        api = new MealDbAPI();
        List<CardData> rand_results = api.getRandomMeal() ;

        // Loop for amount of random meals to show on grid
        for (int i = 0; i < 8; i++) {
            List<CardData> result = api.getRandomMeal();
            if (result != null && !result.isEmpty()) {
                rand_results.add(result.get(0)); // assuming it returns a list with one element
            }
        }
        setGrid(rand_results);

    } // End initialize method


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
                        homeScreen_pfp.setImage(image);
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

} // End HomeScreenController class