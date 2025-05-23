package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * ProfilePageController -> class for the profile page screen
 */
public class ProfilePageController {

    @FXML
    private TextArea Bio_Field;
    @FXML
    private Text DisplayUserName;
    @FXML
    private Text FriendsField;
    @FXML
    private Text HomeField;
    @FXML
    private Text LogOutField;
    @FXML
    private ImageView ProfilePicture;
    @FXML
    private Text RecipeField;
    @FXML
    private Text ShoppingList_Field;
    @FXML
    private TextField get_url_txt;
    @FXML
    private GridPane grid;
    @FXML
    private ScrollPane scroll;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Button report;
    @FXML
    private Button myBio;
    @FXML
    private Button myURL;
    @FXML
    private Text noFavText;


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
    }

    /**
     * load_on_start -> method to Load display name, profile pic, bio upon start
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void load_on_start() throws ExecutionException, InterruptedException {
        loadDisplayName();
        loadProfilePicture();
        loadBio();
    } // End load_on_start method

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        // try/catch block to Load load_on_start method
        try {
            load_on_start();
            displayFavoriteCards();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        } // End try/catch block

        // Screen Switching methods
        HomeField.setOnMouseClicked(event -> {
            try {
                FoodRecipePlatform.setRoot("HomeScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }) ;
        RecipeField.setOnMouseClicked(event -> {
            try {
                FoodRecipePlatform.setRoot("RecipeSearchScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }) ;
        FriendsField.setOnMouseClicked(event -> {
            try {
                FoodRecipePlatform.setRoot("FriendsList"); // NEEDS TO CHANGE TO FRIENDS SCREEN WHEN CREATED
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }) ;
        ShoppingList_Field.setOnMouseClicked(event -> {
            try {
                FoodRecipePlatform.setRoot("ShoppingScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }) ;
        LogOutField.setOnMouseClicked(event -> {
            try {
                FoodRecipePlatform.setRoot("SignIn");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }) ;
        // End Screen Switching
    } // End initialize

    /**
     * changeProfPic -> Method to get file from the file loader and input it into DB
     *                  to hold the position or file path as the url to be called from
     *                  the loader method
     */
    void changeProfPic() throws ExecutionException, InterruptedException {
        profilePictureUrl = null;

        File file = (new FileChooser()).showOpenDialog(ProfilePicture.getScene().getWindow());
        if (file != null) {
            profilePictureUrl = file.toURI().toString();
            ProfilePicture.setImage(new Image(profilePictureUrl)); // Show new image

            String userId = SessionManager.getUserId(); // Document ID of the user

            // Update Firestore
            FoodRecipePlatform.fstore.collection("Users")
                    .document(userId)
                    .update("ProfilePicture", profilePictureUrl)
                    .get(); // Wait until update completes

            // Load the updated profile picture back from Firestore
            DocumentSnapshot snapshot = FoodRecipePlatform.fstore.collection("Users")
                    .document(userId)
                    .get()
                    .get();

            if (snapshot.exists()) {
                String imageUrl = snapshot.getString("ProfilePicture");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        ProfilePicture.setImage(new Image(imageUrl));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error loading image: " + e.getMessage());
                    }
                }
            }
        }
    } // End changeProfPic method

    /**
     * loadBio -> method to load the Bio in DB to its place on the screen upon init
     */
    void loadBio() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        DocumentSnapshot snapshot = FoodRecipePlatform.fstore.collection("Users")
                .document(userId)
                .get()
                .get();

        if (snapshot.exists()) {
            String Bio_in_db = snapshot.getString("Bio");
            if (Bio_in_db != null && !Bio_in_db.isEmpty()) {
                Bio_Field.clear();
                Bio_Field.setText(Bio_in_db); // bio field -> text area in UI -> input from db
            } else {
                Bio_Field.setText("Please enter your Bio");
            }
        } else {
            DisplayUserName.setText("Bio not found");
        }
    } // End loadBio method


    /**
     * loadDisplayName -> method to load the Display name in DB to its place on the screen upon init
     */
    void loadDisplayName() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        DocumentSnapshot snapshot = FoodRecipePlatform.fstore.collection("Users")
                .document(userId)
                .get()
                .get();

        if (snapshot.exists()) {
            String displayName = snapshot.getString("DisplayName");
            if (displayName != null && !displayName.isEmpty()) {
                DisplayUserName.setText(displayName); // Works with javafx.scene.text.Text -> .setText() method
            } else {
                DisplayUserName.setText("Unknown User");
            }
        } else {
            DisplayUserName.setText("User not found");
        }
    } // End loadDisplayName method

    /**
     * loadProfilePicture -> method to load the saved image in DB to its place on the screen upon init
     */
    void loadProfilePicture() {
        String userId = SessionManager.getUserId();

        FoodRecipePlatform.fstore.collection("Users")
                .document(userId)
                .get()
                .addListener(() -> {
                    try {
                        DocumentSnapshot snapshot = FoodRecipePlatform.fstore
                                .collection("Users")
                                .document(userId)
                                .get()
                                .get();

                        if (snapshot.exists()) {
                            String imageUrl = snapshot.getString("ProfilePicture");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                ProfilePicture.setImage(new Image(imageUrl));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Runnable::run);
    } // End loadProfilePicture method

    /**
     * setImage_DB -> method to get the input from url text field and save it into DB
     */
    void setImage_DB() {
        String userId = SessionManager.getUserId();
        String inputUrl = get_url_txt.getText();

        if (inputUrl == null || inputUrl.isEmpty()) {
            showAlert("Input Error", "Please enter a valid image URL.", null);
            return;
        }

        try {
            // Try loading image to validate URL
            Image testImage = new Image(inputUrl);
            ProfilePicture.setImage(testImage); // Set ProfilePicture in UI -> from url saved in db

            DocumentReference userDocRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId);
            userDocRef.update("ProfilePicture", inputUrl).get(); // wait for update in db

            showAlert("Success", "Profile picture updated!", "Profile picture updated Successfully!");
            System.out.println("Profile picture URL updated to: " + inputUrl);

        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid Image URL", "Could not load image from the provided URL.");
            System.err.println("Image loading failed: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Database Error", e.getMessage());
            e.printStackTrace();
        }
    } // End setImage_DB method


    /**
     * set_Bio_in_DB -> Method to get text from Bio text field and input it into DB
     */
    void set_Bio_in_DB() {
        String userId = SessionManager.getUserId();
        String inputBio = Bio_Field.getText();

        if (inputBio == null || inputBio.isEmpty()) {
            showAlert("Input Error", "Please enter a valid image Bio.", "Please enter a valid image Bio.");
            return;
        }
        try {
            Bio_Field.setText(inputBio);

            DocumentReference userDocRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId);
            userDocRef.update("Bio", inputBio).get(); // wait for Bio to update in db

            showAlert("Success", "User Bio has been updated!", "Successfully updated user bio!");
            System.out.println("Profile picture URL updated to: " + inputBio);

        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid Bio Text", "Could not load Bio from text field.");
            System.err.println("Bio loading failed: " + e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Database Error", e.getMessage());
            e.printStackTrace();
        }
    } // End setImage_DB method

    /**
     * displayFavoriteCards -> goes through db and gets favorited items ->
     * -> then calls set on grid method to display it on the grid
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void displayFavoriteCards() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();
        List<String> get_F_Meal_Ids = new ArrayList<>();

        // Get the document ID for this ingredient
        CollectionReference favoritedFoodListRef = FoodRecipePlatform.fstore
                .collection("Users")
                .document(userId)
                .collection("favoritedFoods");

        ApiFuture<QuerySnapshot> future = favoritedFoodListRef.get();
        QuerySnapshot querySnapshot = future.get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            String FavoritedID = document.getString("FavoritedID");
            Boolean isfavorited = document.getBoolean("favorite");
            System.out.println("Document data: " + document.getData());


            if (FavoritedID != null && Boolean.TRUE.equals(isfavorited) ) {
                get_F_Meal_Ids.add(FavoritedID);
                System.out.println("Favorited food id = " + FavoritedID);
            } else {
                System.out.println("Skipping invalid or un-favorited item: " + FavoritedID);
            }
        }

        api = new MealDbAPI();

        // Get favorite meals
        List<CardData> FAV_results = new ArrayList<>();
        for (String mealId : get_F_Meal_Ids) {
            System.out.println("Fetching meal ID: " + mealId);
            List<CardData> result = api.getMealDetails(mealId);
            if (result != null && !result.isEmpty()) {
                FAV_results.add(result.get(0));
                System.out.println("Added meal: " + result.get(0).getFoodName());
            } else {
                System.out.println("No result found for ID: " + mealId);
            }
        }

        System.out.println("Total favorite meals to display: " + FAV_results.size());

        // call setGrid method
        setGrid(FAV_results);

    } // End displayFavoriteCards method

    private void setGrid(List<CardData> inputCardList) {
        // Method to Display Card data on grin in scroll pane
        grid.getChildren().clear();
        cards = inputCardList;

        System.out.println("Cards to display = " + cards.size());
        if (cards.size() > 0) {
            noFavText.setDisable(true);
        }


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


    // Buttons and menu Buttons section
    @FXML
    void Update_Bio_Button(ActionEvent event) {
        Platform.runLater(() -> {
            popNode(myBio);
            set_Bio_in_DB();
        });
//        set_Bio_in_DB();
    } // End Update_Bio_Button
    @FXML
    void menu_change_Ppic(ActionEvent event) throws ExecutionException, InterruptedException {
        changeProfPic();
    } // End menu_change_Ppic
    @FXML
    void Update_Photo_URL(ActionEvent event) {
        Platform.runLater(() -> {
            popNode(myURL);
            setImage_DB();
        });
//        setImage_DB();
    } // End Update_Photo_URL
    @FXML
    void set_DarkTheme(ActionEvent event) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End set_DarkTheme
    @FXML
    void set_LightTheme(ActionEvent event) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End set_LightTheme

    @FXML
    void report_Issue_Button(ActionEvent event) {
        Platform.runLater(() -> {
            popNode(report);
            showAlert("Report Issue", "Report Issue", "Sorry at this time we are not able to take an issue report !");
        });

    } // End report_Issue_Button

    private void popNode(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

} // End ProfilePageController