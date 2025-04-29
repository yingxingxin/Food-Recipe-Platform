package org.example.foodrecipeplatform;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class FriendsListTestApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/foodrecipeplatform/FriendsList.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Test Friends List");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
