package org.example.foodrecipeplatform.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.ShoppingList;

import java.io.IOException;

public class PickedRecipeScreenController
{
    @FXML
    public Text nameText;
    @FXML
    public Text categoryText;
    @FXML
    public Text areaText;

    @FXML
    public ImageView recipeImageView;

    @FXML
    public TextArea ingredientTextArea;
    @FXML
    public TextArea instructionsTextArea;


    @FXML
    public Button backButton;
    @FXML
    public Button favoriteButton;
    @FXML
    public Button shoppinglistButton;

    @FXML
    public Hyperlink profileHyperLink;
    @FXML
    public Hyperlink shoppingHyperLink;
    @FXML
    public Hyperlink homeHyperLink;

    private String currentMealId;
    private ShoppingList shoppingList;


    @FXML
    void initialize()
    {
        instructionsTextArea.setWrapText(true);
        ingredientTextArea.setWrapText(true);
        // make the back button save the results on the screen?

        shoppingList = new ShoppingList();

    }

    public void setCurrentMealId(String mealId) {
        this.currentMealId = mealId;
    }

    @FXML
    void backButtonClicked(ActionEvent event) throws IOException
    {
        FoodRecipePlatform.setRoot("RecipeSearchScreen");
    }

    @FXML
    void favoriteButtonClicked(ActionEvent event) throws IOException
    {
        System.out.println("favorite button clicked");
    }

    @FXML
    void shoppinglistButtonClicked(ActionEvent event) throws IOException
    {
        System.out.println("Add to shopping list button clicked");

        if (currentMealId != null && !currentMealId.isEmpty()) {
            // Add the current recipe's ingredients to the shopping list
            shoppingList.addIngredients(currentMealId);

            // Give feedback to the user
            shoppinglistButton.setText("Added to Shopping List ✓");

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() ->
                            shoppinglistButton.setText("Add to Shopping List"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            System.out.println("No meal ID available to add to shopping list");
        }
    }

    @FXML
    void profileHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("profile hyperlink clicked");
        FoodRecipePlatform.setRoot("ProfilePage");
    }

    @FXML
    void shoppingHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("shopping hyperlink clicked");
        FoodRecipePlatform.setRoot("ShoppingScreen");
    }

    @FXML
    void homeHyperLinkClicked(ActionEvent event) throws IOException
    {
        System.out.println("home hyperlink clicked");
        FoodRecipePlatform.setRoot("HomeScreen");
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

    public void setCategoryText(String category)
    {
        categoryText.setText(category);
    }
}
