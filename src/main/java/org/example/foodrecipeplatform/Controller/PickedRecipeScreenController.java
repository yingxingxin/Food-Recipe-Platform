package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.ShoppingList;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PickedRecipeScreenController
{
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

    @FXML
    void initialize()
    {
        // method to get profile picture from database
        getImage_DB();

        instructionsTextArea.setWrapText(true);
        ingredientTextArea.setWrapText(true);
        // make the back button save the results on the screen?

        shoppingList = new ShoppingList();
    }

    public void setCurrentMealId(String mealId) {
        this.currentMealId = mealId;
    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException
    {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    }

    @FXML
    void favoriteButtonClicked(ActionEvent event) throws IOException
    {
        System.out.println("favorite button clicked");
    }

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
                    javafx.application.Platform.runLater(() ->
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
