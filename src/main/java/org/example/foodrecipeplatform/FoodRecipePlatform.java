package org.example.foodrecipeplatform;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// testing commit to see if it still hides the key
public class FoodRecipePlatform extends Application {
    public static Scene scene;

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    @Override
    public void start(Stage stage) throws IOException {
        fstore = contxtFirebase.firebase();
        fauth = FirebaseAuth.getInstance();

        scene = new Scene(loadFXML("SignIn"), 1000, 800); // RecipeSearchScreen

        String css = getClass().getResource("/css/default.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("The Flavor Vault !");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    // need public static Scene, loadFXML
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FoodRecipePlatform.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}