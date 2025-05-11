package org.example.foodrecipeplatform;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

// testing commit to see if it still hides the key
public class FoodRecipePlatform extends Application {
    public static Scene scene;
    public static Firestore fstore;
    public static FirebaseAuth fauth;
    private final FirestoreContext contxtFirebase = new FirestoreContext();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        fstore = contxtFirebase.firebase();
        fauth = FirebaseAuth.getInstance();
//        scene = new Scene(loadFXML("SignIn"), 1200, 800); // RecipeSearchScreen

        // Load and show splash screen
        Parent splashRoot = loadFXML("splashscreen");
        scene = new Scene(splashRoot, 1200, 800);

        stage.setTitle("The Flavor Vault !");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();


        // Fade out the splash and show SignIn
        FadeTransition fade = new FadeTransition(Duration.seconds(2), splashRoot);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            try {
                Parent loginRoot = loadFXML("SignIn");
                scene.setRoot(loginRoot);

                // Fade in the login screen
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), loginRoot);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fade.play();

    } // End start methods


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