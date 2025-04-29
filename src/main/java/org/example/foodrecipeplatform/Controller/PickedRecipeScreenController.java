package org.example.foodrecipeplatform.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;

public class PickedRecipeScreenController
{
    @FXML
    public Text nameText;
    @FXML
    public ImageView recipeImageView;
    @FXML
    public TextArea ingredientTextArea;
    @FXML
    public TextArea instructionsTextArea;
    @FXML
    public Text areaText;

    @FXML
    public Button backButton;
    @FXML
    public Button favoriteButton;
    @FXML
    public Button shoppinglistButton;


    @FXML
    void initialize()
    {
        instructionsTextArea.setWrapText(true);
        // make the back button save the results on the screen?
    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException
    {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    }

    public void setRecipeName(String recipeName)
    {
        nameText.setText(recipeName);
    }

    public void setRecipeImage(Image recipeImage)
    {
        recipeImageView.setImage(recipeImage);
    }

    public void setArea(String area)
    {
        areaText.setText(area);
    }

    public void setIngredientTextArea(String ingredient)
    {
        ingredientTextArea.setText(ingredient);
    }

    public void setInstructionsTextArea(String instructions)
    {
        instructionsTextArea.setText(instructions);
    }
}
