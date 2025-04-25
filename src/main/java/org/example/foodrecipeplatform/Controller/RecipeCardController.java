package org.example.foodrecipeplatform.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.foodrecipeplatform.CardData;

public class RecipeCardController {

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

        nameTooltip.textProperty().setValue(cardData.getFoodName());

        //Image image = new Image(getClass().getResourceAsStream( cardData.getImageURL()));
        Image image = new Image(cardData.getImageURL());
        img.setImage(image);

        recipeDesc.setText(cardData.getDescription());
    }



}
