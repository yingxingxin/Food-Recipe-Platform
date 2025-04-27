package org.example.foodrecipeplatform.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeScreenController implements Initializable
{


    @FXML
    void OpenFoodGeneratorScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    }
    @FXML
    void OpenProfileScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("ProfilePage");
    }
    @FXML
    void OpenShoppingListScreen(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("ShoppingScreen");
    }


    @FXML
    private ScrollPane scroll;
    @FXML
    private GridPane grid;


    // example implement - hard code
    List<CardData> cards = new ArrayList<>();


    private List<CardData> getData(){
        List<CardData> cards = new ArrayList<>();
        CardData card;

        for (int i=0;i<16;i++){
            // Default Card will be a Burger for now will turn into empty slot soon
            // ADD to Homepage should be the way the cards which are in the favorite screen should be added
            card = new CardData("Burger",
                    "This the New Krabby Patty Burger Recipe",
                    "/images/image.png");
            cards.add(card);
        }
        return cards;
    }

    /**
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        grid.getChildren().clear();
        cards.addAll(getData());

        int row = 1;
        int col = 0;

        try {
            for (int i = 0; i < cards.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL fxmlPath = getClass().getResource("/org/example/foodrecipeplatform/RecipeCard.fxml");
                // System.out.println("FXML path = " + fxmlPath); // Debug

                if (fxmlPath == null) {
                    System.out.println("Could not find RecipeCard.fxml");
                    continue;
                }

                fxmlLoader.setLocation(fxmlPath);
                AnchorPane anchorPane = fxmlLoader.load();

                RecipeCardController recipeCardController = fxmlLoader.getController();
                recipeCardController.setData(cards.get(i));

                if (col == 3) {
                    col = 0;
                    row++;
                }

                grid.add(anchorPane, col++, row);
                GridPane.setMargin(anchorPane, new Insets(10));


            }
        } catch (IOException e) {
            System.out.println("Failed to load RecipeCard.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("General error in initialize");
            e.printStackTrace();
        }
    }






}




//implement needed
        /*
    private List<CardData> loadCards(String fileName){
        List<CardData> cards = new ArrayList<>();
        try {
            // needs implementation
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return cards;
    }

         */
