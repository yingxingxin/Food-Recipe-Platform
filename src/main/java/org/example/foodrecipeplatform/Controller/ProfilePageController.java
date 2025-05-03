package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.internal.GetAccountInfoResponse;
import com.google.firebase.remoteconfig.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
    public void initialize() {
        // try/catch block to Load load_on_start method
        try {
            load_on_start();
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
                FoodRecipePlatform.setRoot("HomeScreen"); // NEEDS TO CHANGE TO FRIENDS SCREEN WHEN CREATED
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



    // Buttons and menu Buttons section
    @FXML
    void Update_Bio_Button(ActionEvent event) {
        set_Bio_in_DB();
    } // End Update_Bio_Button
    @FXML
    void menu_change_Ppic(ActionEvent event) throws ExecutionException, InterruptedException {
        changeProfPic();
    } // End menu_change_Ppic
    @FXML
    void Update_Photo_URL(ActionEvent event) {
        setImage_DB();
    } // End Update_Photo_URL
    @FXML
    void set_DarkTheme(ActionEvent event) {

    } // End set_DarkTheme
    @FXML
    void set_LightTheme(ActionEvent event) {

    } // End set_LightTheme
    @FXML
    void report_Issue_Button(ActionEvent event) {
        showAlert("Report Issue", "Report Issue", "Sorry at this time we are not able to take an issue report !");
    } // End report_Issue_Button

} // End ProfilePageController