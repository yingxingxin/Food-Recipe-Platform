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

    // To store the retrieved URL
    private String profilePictureUrl;

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


    // example implement - hard code
    List<CardData> cards = new ArrayList<>();


    private List<CardData> getData(){
        List<CardData> cards = new ArrayList<>();
        CardData card;

        for (int i=0;i<16;i++){
            // Default Card will be a Burger for now will turn into empty slot soon
            // ADD to Homepage should be the way the cards which are in the favorite screen should be added
            card = new CardData("Burger",
                    "This the New Krabby Patty Burger Recipe",
                    "/images/image.png");
            cards.add(card);
        }
        return cards;
    } // End getData method for List<CardData>


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

        grid.getChildren().clear();
        cards.addAll(getData());

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




//implement needed
        /*
    private List<CardData> loadCards(String fileName){
        List<CardData> cards = new ArrayList<>();
        try {
            // needs implementation
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return cards;
    }

         */

