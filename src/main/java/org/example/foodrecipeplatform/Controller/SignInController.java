package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.example.foodrecipeplatform.Controller.SessionManager;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * SignInController class -> logic to get user info from database -> authenticate -> sign in
 */
public class SignInController {
    // to connenct buttons, make sure that the id is fx:id= & make sure the controller is set to this class
    @FXML
    private Button sign_inButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Text messageText;
    @FXML
    private ImageView SignInLogo;
    @FXML
    private VBox MainVbox;

    @FXML
    void showAlert( String header, String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    } // End showAlert method


    public void initialize() {

    } // End initialize method

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }


    // to make sure the buttons are connected to the method, add onAction to the button in fxml
    @FXML
    void sign_inButtonClicked (ActionEvent event) throws IOException {
        System.out.println("Sign in clicked");

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // if signIn returns true if matching username & password was found & goes to the primary screen
        if (signIn(username, password))
        {
            System.out.println(username + " is in database");
            // Pop animation
            ScaleTransition st = new ScaleTransition(Duration.millis(200), sign_inButton);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.2);
            st.setToY(1.2);
            st.setAutoReverse(true);
            st.setCycleCount(2);

            st.setOnFinished(e -> {
                try {
                    switchToPrimary();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            st.play();

        }
        else {

            System.out.println(username + " is not in database or password wrong");
        }

    } // End sign_inButtonClicked method


    /**
     * signIn method
     * - takes in username and password which is sent from the username and password text fields.
     * It goes through the firestore database user's document and checks if there's a matching username &
     * password. Returns true if a matching was found & false if not
     * @param username - the email that was taken from the username textfield
     * @param password - the password that was taken from the password textfield
     * @return true if input matches database
     */
    public boolean signIn(String username, String password) {

        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").get();

        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();

            if(!documents.isEmpty()) // as longs as documents list ! empty
            {
                System.out.println("Checking if user is in database...");

                for (QueryDocumentSnapshot document : documents)
                {
                    String user = document.getString("UserName");
                    String pass = document.getString("Password");

                    System.out.println(document.getId() + " => " + user + " " + pass);

                    if (username.equals(user) && password.equals(pass)) {
                        SessionManager.setUserId(document.getId());
                        System.out.println("Logged in as: " + user + " (Doc ID: " + document.getId() + ")");
                        return true;
                    }

                    // check if username was found but password wrong
                    if (username.equals(user) && !password.equals(pass)) {

                        Platform.runLater(() -> {
                            shakeNode(sign_inButton);
                            shakeNode(MainVbox);
                            showAlert("Incorrect Sign In", "Sign in clicked!", username + " has entered an Incorrect Password");

                        });
                        return false;
                    }
                }

                // if no match found after checking all
                Platform.runLater(() -> {
                    shakeNode(sign_inButton);
                    shakeNode(MainVbox);
                    showAlert("Incorrect Sign In", "Sign in clicked!", "Sorry, incorrect Username or Password");
                });


            }
            else
            {
                System.out.println("No Users in the database");
            }

        } catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return false;
    } // End signIn Method


    @FXML
    void registerButtonClicked (ActionEvent event) throws IOException {
        // Pop animation
        ScaleTransition st = new ScaleTransition(Duration.millis(200), registerButton);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        // Call switchToRegisterScreen() only after animation finishes
        st.setOnFinished(e -> {
            try {
                switchToRegisterScreen();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        st.play();

    } // End registerButtonClicked button

    private void switchToPrimary() throws IOException {
        FoodRecipePlatform.setRoot("HomeScreen");
    } // End switchToPrimary Method

    private void switchToRegisterScreen() throws IOException {
        FoodRecipePlatform.setRoot("RegistrationScreen");
    } // End switchToRegisterScreen Method

    @FXML
    void Forgot_Password_link(ActionEvent event) {
        showAlert("Follow steps to retrieve Password ", " Forgot Password !", "Sorry at this time we can not retrieve your password !");
    } // End Forgot_Password_link Method


} // End SignInController class