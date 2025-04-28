package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RegistrationScreenController
{
    @javafx.fxml.FXML
    public void initialize() {
    }

    @FXML
    private Button CreateAccountBttn;

    @FXML
    private TextField EmailTextField;

    @FXML
    private Hyperlink Login_Redirect;

    @FXML
    private TextField Pass1TextField;

    @FXML
    private TextField Pass2TextField;

    @FXML
    public TextField getDisplayName() {
        return DisplayName;
    }

    @FXML
    private TextField DisplayName;

    @FXML
    private Text messageTextRS;

    @FXML
    void Login_RedirectBttn(ActionEvent event) throws IOException {
        switchToSignInScreen();
    }

    private void switchToSignInScreen() throws IOException {
        FoodRecipePlatform.setRoot("SignIn");
    }


//    @FXML
//    void CreateAccountButtonClicked(ActionEvent event) {
//
//    }







    @FXML
    void CreateAccountButtonClicked (ActionEvent event)
    {
        System.out.println("register in clicked");
        if (registerUser())
            addUser();
    }

    /*
        - MAKE SURE YOU ENABLE EMAIL ON AUTHENTICATION TO USE THIS
        - phone email and phone number needs to be unique for register to work
        - email is format checked to see if there's an @ & .com
        - phone number is checked to see if length is correct
        - phone number needs to be in this format +12344567899 -> +1 123 123 1234
        - password needs to be at least 6 letters long
    */
    public boolean registerUser() {

        // Need to implement userName Text-field will be (Profiles userName)

        try
        {
            // Check if Passwords Match
            if (Objects.equals(Pass1TextField.getText(), Pass2TextField.getText())) {

                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setDisplayName(DisplayName.getText())
                        .setEmail(EmailTextField.getText())
                        .setEmailVerified(false)
                        .setPassword(Pass1TextField.getText())
                        .setPhotoUrl("")
                        .setDisabled(false);

//        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
//
//        try
//        {
//            request.setEmail(usernameTextField.getText()).setEmailVerified(false)
//                    .setPassword(passwordTextField.getText()).setDisabled(false);
//        }
//        catch (IllegalArgumentException e)
//        {
//            System.out.println(e.getMessage());
//            return false;
//        }


                UserRecord userRecord;

                userRecord = FoodRecipePlatform.fauth.createUser(request);
                System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                        + " check Firebase > Authentication > Users tab");
                return true;

            }
            else {
                messageTextRS.setText("Passwords do not match !") ;
                System.out.println("Passwords do not match !");
                return false;
            }
        }   // End Try Block

//        catch (FirebaseAuthException ex)
//        {
//            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("Error creating a new user in the firebase");
//            return false;
//        }
        catch (IllegalArgumentException | FirebaseAuthException e)
        {
            String message = e.getMessage();
            if (message != null && message.contains("exists"))
            {
                System.out.println("Email already in use");
                messageTextRS.setText("Email already in use");
            }
            else
            {
                System.out.println("Email not in valid format or password length must be >6");
                messageTextRS.setText("Email not in valid format or password length must be >6");
            }

            return false;
        }
    }   // End Catch Block


    /*
        add user
    */
    public void addUser() {
        // creates the Users collection in the db
        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(UUID.randomUUID().toString());

        // creates the fields in the Users collection
        Map<String, Object> data = new HashMap<>();
        data.put("UserName", EmailTextField.getText());
        data.put("Password", Pass1TextField.getText());
        data.put("DisplayName", DisplayName.getText());
        data.put("ProfilePicture", "");

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }








} // End RegistrationScreenController





//    @FXML
//    void registerButtonClicked (ActionEvent event)
//    {
//        System.out.println("register in clicked");
//        if (registerUser())
//            addUser();
//    }
//
//    /*
//        - MAKE SURE YOU ENABLE EMAIL ON AUTHENTICATION TO USE THIS
//        - phone email and phone number needs to be unique for register to work
//        - email is format checked to see if there's an @ & .com
//        - phone number is checked to see if length is correct
//        - phone number needs to be in this format +12344567899 -> +1 123 123 1234
//        - password needs to be at least 6 letters long
//    */
//    public boolean registerUser() {
//        try
//        {
//            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
//                    .setEmail(usernameTextField.getText())
//                    .setEmailVerified(false)
//                    .setPassword(passwordTextField.getText())
//                    .setDisabled(false);
//
////        UserRecord.CreateRequest request = new UserRecord.CreateRequest();
////
////        try
////        {
////            request.setEmail(usernameTextField.getText()).setEmailVerified(false)
////                    .setPassword(passwordTextField.getText()).setDisabled(false);
////        }
////        catch (IllegalArgumentException e)
////        {
////            System.out.println(e.getMessage());
////            return false;
////        }
//
//
//            UserRecord userRecord;
//
//            userRecord = FoodRecipePlatform.fauth.createUser(request);
//            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
//                    + " check Firebase > Authentication > Users tab");
//            return true;
//
//        }
//
////        catch (FirebaseAuthException ex)
////        {
////            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
////            System.out.println("Error creating a new user in the firebase");
////            return false;
////        }
//        catch (IllegalArgumentException | FirebaseAuthException e)
//        {
//            String message = e.getMessage();
//            if (message != null && message.contains("exists"))
//            {
//                System.out.println("Email already in use");
//                messageText.setText("Email already in use");
//            }
//            else
//            {
//                System.out.println("Email not in valid format or password length must be >6");
//                messageText.setText("Email not in valid format or password length must be >6");
//            }
//
//            return false;
//        }
//    }
//
//
//    /*
//        add user
//    */
//    public void addUser() {
//        // creates the Users collection in the db
//        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(UUID.randomUUID().toString());
//
//        // creates the fields in the Users collection
//        Map<String, Object> data = new HashMap<>();
//        data.put("UserName", usernameTextField.getText());
//        data.put("Password", passwordTextField.getText());
//
//        //asynchronously write data
//        ApiFuture<WriteResult> result = docRef.set(data);
//    }