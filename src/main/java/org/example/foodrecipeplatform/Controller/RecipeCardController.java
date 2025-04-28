package org.example.foodrecipeplatform.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;

import java.io.IOException;

public class RecipeCardController {
    @FXML
    public AnchorPane rootPane;
    @FXML
    private ImageView img;

    @FXML
    private Label recipeDesc;

    @FXML
    private Label recipeName;

    private CardData cardData;

    @FXML
    public Tooltip nameTooltip;

    @FXML
    void initialize()
    {
        recipeName.setPrefWidth(200);
        recipeName.setWrapText(true);
        recipeName.setPrefHeight(40);
    }


    public void setData(CardData cardData) {
        this.cardData = cardData;
        recipeName.setText(cardData.getFoodName());

        nameTooltip.textProperty().setValue(cardData.getFoodName()); // showing popup

        //Image image = new Image(getClass().getResourceAsStream( cardData.getImageURL()));
        Image image = new Image(cardData.getImageURL());
        img.setImage(image);

        recipeDesc.setText(cardData.getDescription());

        // when card is clicked, goes to picked recipe screen and sends data there
        rootPane.setOnMouseClicked(event -> {
            cardClicked();
        });
    }


    // make api get idMeal, meal name, area, category, ingredients, instructions, image, ingredients with measurements in
    // string array, 9 array
    private void cardClicked()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/foodrecipeplatform/PickedRecipeScreen.fxml"));

        Parent root;
        try {
            root = loader.load(); // load() is necessary for @FXML injection
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PickedRecipeScreenController pickedScreen = loader.getController();

        System.out.println("Card clicked: " + cardData.getFoodName());
        new MealDbAPI().getAllDetails(cardData.getMealId());

        pickedScreen.setRecipeName(cardData.getFoodName());
        pickedScreen.setRecipeImage(img.imageProperty().getValue());

        Scene scene = rootPane.getScene();
        scene.setRoot(root);
    }


}
