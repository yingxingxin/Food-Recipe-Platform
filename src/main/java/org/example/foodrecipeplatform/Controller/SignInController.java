package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.example.foodrecipeplatform.Controller.SessionManager;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SignInController
{
    // to connenct buttons, make sure that the id is fx:id= & make sure the controller is set to this class
    @FXML
    private Button sign_inButton;

    @FXML
    private Button registerButton;

    public TextField getUsernameTextField() {
        return usernameTextField;
    }

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Text messageText;

    @FXML
    private ImageView SignInLogo;

    @FXML
    private AnchorPane sceneAnchor;

    @FXML
    private VBox MainVbox;




    public void initialize()
    {

        //MainVbox.setStyle("-fx-background-color: #ff0");

        //fitWidthProperty().bind(MainVbox.widthProperty());





        //AccessDataView accessDataViewModel = new AccessDataView();
        //usernameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        //register.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
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
            try {
                switchToPrimary();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            System.out.println(username + " is not in database or password wrong");
    }


    @FXML
    void registerButtonClicked (ActionEvent event) throws IOException {
        switchToRegisterScreen();
    }





    /*
        signIn method
        - takes in username and password which is sent from the username and password text fields.
        It goes through the firestore database user's document and checks if there's a matching username &
        password. Returns true if a matching was found & false if not
        @param username - the email that was taken from the username textfield
        @param password - the password that was taken from the password textfield
     */
    public boolean signIn(String username, String password)
    {
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").get();

        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Checking if user is in database...");

                // user and pass are used to store the usernames and passwords in the firestore database
                String user = "", pass;
                boolean found = false;

                // going through the document list
                for (QueryDocumentSnapshot document : documents)
                {
                    System.out.println(document.getId() + " => " + document.getData().get("UserName")
                            + " " + document.getData().get("Password"));

                    user = document.getData().get("UserName").toString();
                    pass = document.getData().get("Password").toString();

                    // exits the method and returns true when matching username & password was found
                    if (username.equals(user) && password.equals(pass))
                        SessionManager.setUserId(document.getId());
                    System.out.println("Logged in as: " + user + " (Doc ID: " + document.getId() + ")");
                        return true;
                }
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
    }






    private void switchToPrimary() throws IOException {
        FoodRecipePlatform.setRoot("HomeScreen");
    }


    private void switchToRegisterScreen() throws IOException {
        FoodRecipePlatform.setRoot("RegistrationScreen");
    }


}