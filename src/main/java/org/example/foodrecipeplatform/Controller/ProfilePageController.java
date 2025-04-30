package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfilePageController {

    @FXML
    private TextArea Bio_Field;
    @FXML
    private Text DisplayUserName;
    @FXML
    private Text FriendsField;
    @FXML
    private ListView<String> Friends_ListView;
    @FXML
    private TextField Friends_TextField;
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

    // newProfilePicture
    private String newProfilePicture = "";
    public String getNewProfilePicture() {
        return newProfilePicture;
    }
    public void setNewProfilePicture(String newProfilePicture) {
        this.newProfilePicture = newProfilePicture;
    }

    @FXML
    void menu_change_Ppic(ActionEvent event) throws ExecutionException, InterruptedException {
        changeProfPic();
    }


    // prof pic being stored from file -> should use the actual url to keep it after save
    //String currUserID = SessionManager.getCurrentUserName();

    @FXML
    void initialize() {
        String currUserID = SessionManager.getUserId().toString();
        System.out.println("Current Username: " + currUserID);

        System.out.println(currUserID);
        try {
            ApiFuture<QuerySnapshot> future = (ApiFuture<QuerySnapshot>) FoodRecipePlatform.fstore.collection("Users")
                    .whereEqualTo("UserName", currUserID)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            //document.getData().get("UserName").toString()

            for (QueryDocumentSnapshot document : documents) {
                String profilePicUrl = document.getData().get("ProfilePicture").toString();

                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                    ProfilePicture.setImage(new Image(profilePicUrl));
                    setNewProfilePicture(profilePicUrl);
                }

                String displayName = document.getData().get("DisplayName").toString();
                if (displayName != null ) {
                    DisplayUserName.setText(displayName);
                }

            }
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        getNewProfilePicture();






        // Screen Switching
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



    } // End initialize

    void changeProfPic() throws ExecutionException, InterruptedException {
        File file = (new FileChooser()).showOpenDialog(ProfilePicture.getScene().getWindow());
        if (file != null) {
            newProfilePicture = file.toURI().toString();
            ProfilePicture.setImage(new Image(newProfilePicture));

            String currUserID = SessionManager.getUserId().toString();

            ApiFuture<QuerySnapshot> future = (ApiFuture<QuerySnapshot>) FoodRecipePlatform.fstore.collection("Users")
                    .whereEqualTo("UserName", currUserID)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            String docID = documents.get(0).getId();

            FoodRecipePlatform.fstore.collection("Users")
                    .document(docID)
                    .update("ProfilePicture", newProfilePicture);
        }
    }

    @FXML
    void set_DarkTheme(ActionEvent event) {

    }

    @FXML
    void set_LightTheme(ActionEvent event) {

    }


    @FXML
    void report_Issue_Button(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Issue");
        alert.setContentText("Sorry at this time we are not able to take an issue report !");
        alert.showAndWait();
    }
} // End ProfilePageController









//    @FXML
//    void friendRequestButton(ActionEvent event) {
//        // method to send the userName to friends list in database
//        // -> friends list screen should then display the friends added in DB
//    }


// creating list view for showing ingredients
//        ObservableList<String> myFList = FXCollections.observableArrayList(); //DisplayUserName.getText()
//        FilteredList<String> filtered_Friends = new FilteredList<>(myFList, s -> true);
//
//        Friends_TextField.textProperty().addListener((obs, oldVal, newVal) -> {
//            filtered_Friends.setPredicate(item -> item.toLowerCase().contains(newVal.toLowerCase()));
//        });
//
//        Friends_ListView.setItems(filtered_Friends);
//
//        Friends_ListView.setOnMouseClicked(e -> {
//            String selected = Friends_ListView.getSelectionModel().getSelectedItem();
//            if (selected != null) {
//                System.out.println("Selected: " + selected);
//                // Do something with it (e.g. update a label or pass to another scene)
//
//            }
//        });