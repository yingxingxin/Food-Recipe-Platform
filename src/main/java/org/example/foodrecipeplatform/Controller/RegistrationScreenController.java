package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class RegistrationScreenController {

    @javafx.fxml.FXML
    public void initialize() {
    } // End initialize method

    @FXML
    private TextField EmailTextField;
    @FXML
    private TextField Pass1TextField;
    @FXML
    private TextField Pass2TextField;
    @FXML
    private TextField DisplayName;
    @FXML
    private Text messageTextRS;
    @FXML
    private Button createAccount;

    public String getDefauly_PFP_URL() {
        return defauly_PFP_URL;
    }
    public void setDefauly_PFP_URL(String defauly_PFP_URL) {
        this.defauly_PFP_URL = defauly_PFP_URL;
    }

    private String defauly_PFP_URL = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png" ;

    @FXML
    void showAlert( String header, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    } // End showAlert method

    @FXML
    void Login_RedirectBttn(ActionEvent event) throws IOException {
        switchToSignInScreen();
    } // End Login_RedirectBttn Button

    private void switchToSignInScreen() throws IOException {
        FoodRecipePlatform.setRoot("SignIn");
    } // End switchToSignInScreen method


    @FXML
    void CreateAccountButtonClicked (ActionEvent event)
    {
        System.out.println("register button clicked");
        if (registerUser())
            addUser();
    } // End CreateAccountButtonClicked Button


    /**
    - MAKE SURE YOU ENABLE EMAIL ON AUTHENTICATION TO USE THIS
    - phone email and phone number needs to be unique for register to work
    - email is format checked to see if there's an @ & .com
    - phone number is checked to see if length is correct
    - phone number needs to be in this format +12344567899 -> +1 123 123 1234
    - password needs to be at least 6 letters long
    */
    public boolean registerUser() {
        String email = EmailTextField.getText().trim();
        try
        {
            // If the fields are empty
            if (Pass1TextField.getText().isEmpty() || Pass2TextField.getText().isEmpty() ||
                    DisplayName.getText().isEmpty() || EmailTextField.getText().isEmpty() )
            {
                Platform.runLater(() -> {
                    shakeNode(createAccount);
                    showAlert( "Please fill all the fields", "Error", "Please fill all the fields");
                });
                return false;
            }

            if (Pass1TextField.getText().length() < 6 || Pass2TextField.getText().length() < 6){
                //showAlert( "Please fix Password", "Error", "Password length must be > 6");
                Platform.runLater(() -> {
                    shakeNode(createAccount);
                    showAlert( "Please fix Password", "Error", "Password length must be > 6");
                });
                return false;
            }

            // Incorrect UserName format
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
                //showAlert( "Please fix Password", "Error", "Password length must be > 6");
                Platform.runLater(() -> {
                    shakeNode(createAccount);
                    showAlert( "Please fix Email", "Error", "Must enter a valid email");
                });
                return false;
            }


            // Check if Passwords Match
            if (Objects.equals(Pass1TextField.getText(), Pass2TextField.getText())) {

                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setDisplayName(DisplayName.getText())
                        .setEmail(EmailTextField.getText())
                        .setEmailVerified(false)
                        .setPassword(Pass1TextField.getText())
                        .setDisabled(false);

                UserRecord userRecord;
                userRecord = FoodRecipePlatform.fauth.createUser(request);

                Platform.runLater(() -> {
                    popNode(createAccount);
                    System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                            + " check Firebase > Authentication > Users tab");
                    showAlert("Successfully Registered","Registration","Successfully created new user");
                });
                return true;
            }
            else { // if Passwords don't Match
                showAlert( "Please fix Password Fields", "Error", "Passwords do not match");
                messageTextRS.setText("Passwords need to match !") ;
                System.out.println("Passwords do not match !");
                return false;
            }

        } catch (IllegalArgumentException | FirebaseAuthException e)
        {

            String errorMessage = "An error occurred during registration: " + e.getMessage();
            System.err.println(errorMessage); // Log the error
            if (e.getMessage().equals("EMAIL_ALREADY_EXISTS")) {
                showAlert("Error", "Email Already Exists", "This email is already in use. Please use a different email.");
                messageTextRS.setText("Email already in use");
            } else {
                showAlert("Error", "Registration Failed", errorMessage); // Show the full error message
                messageTextRS.setText("Registration failed. Please try again."); //set the message.
            }
            return false;
        } // End Catch Block
    } // End registerUser method



    /**
     * method to add the user data into fireBase -> Data Base
     */
    public void addUser() {
        try {
        // creates the Users collection in the db
        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(UUID.randomUUID().toString());

        // creates the fields in the Users collection
        Map<String, Object> data = new HashMap<>();
        data.put("UserName", EmailTextField.getText());
        data.put("Password", Pass1TextField.getText());
        data.put("DisplayName", DisplayName.getText());
        data.put("ProfilePicture", defauly_PFP_URL);
        data.put("Bio", "");
        data.put("Friends", new ArrayList<String>());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);

    } catch (Exception e) {
        // Handle errors during Firestore write.
        String errorMessage = "Error adding user data to Firestore: " + e.getMessage();
        System.err.println(errorMessage);
        showAlert("Error", "Database Error", "Failed to save user data. Please check your connection.");
    } // End catch

    } // End addUser method

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

} // End RegistrationScreenController