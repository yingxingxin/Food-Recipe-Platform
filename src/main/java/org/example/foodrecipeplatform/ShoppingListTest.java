package org.example.foodrecipeplatform;

import org.example.foodrecipeplatform.Controller.SessionManager;
import org.example.foodrecipeplatform.Controller.ShoppingScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


// Testing class for shopping list functionality
public class ShoppingListTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize Firebase
        FirestoreContext firestoreContext = new FirestoreContext();
        FoodRecipePlatform.fstore = firestoreContext.firebase();

        // For testing, set a user ID
        // In a real app, this would be set during login
        SessionManager.setUserId("test_user_123");

        System.out.println("Loading FXML...");

        // Use the FXML loading method from main application
        FXMLLoader loader = new FXMLLoader(
                FoodRecipePlatform.class.getResource("ShoppingScreen.fxml")
        );
        Parent root = loader.load();

        System.out.println("FXML loaded successfully");

        // Get controller reference
        ShoppingScreenController controller = loader.getController();

        // Create the scene
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setTitle("Shopping List Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Test adding ingredients from a meal
        System.out.println("Testing adding ingredients to shopping list...");
        String testMealId = "52772"; // Teriyaki Chicken Casserole
        controller.addIngredientsFromMeal(testMealId);
    }

    public static void main(String[] args) {
        launch(args);
    }
}