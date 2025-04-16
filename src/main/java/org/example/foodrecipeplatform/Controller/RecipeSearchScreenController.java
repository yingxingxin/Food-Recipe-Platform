package org.example.foodrecipeplatform.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.MealDbAPI;

import java.util.List;

public class RecipeSearchScreenController
{
    @FXML
    public Button searchButton;
    @FXML
    public Button RandomFoodButton;
    @FXML
    public TextArea resultTextArea;
    @FXML
    public TextField searchTextField;
    @FXML
    public Hyperlink shoppingHyperlink;
    @FXML
    public Hyperlink HomePageHyperlink;
    @FXML
    public Hyperlink profileHyperlink;
    @FXML
    public ComboBox<String> FirstLetterComboBox;
    @FXML
    public ComboBox<String> CountryComboBox;

    MealDbAPI api;

    @FXML
    void initialize()
    {
        api = new MealDbAPI();
    }

    @FXML
    void searchButtonClicked(ActionEvent event)
    {
        System.out.println("Get Button clicked");
        getRecipe();
    }

    @FXML
    void randomFoodButtonClicked(ActionEvent event)
    {
        System.out.println("Random Food Button Clicked");
        randomRecipe();
    }

    // make sure the search bar input is valid: no empty string, numbers
    // returns false if empty
    // returns false if string matches letters, false bc it's going to negate it in the if statement in getRecipe method
    private boolean checkSearchBar(TextField searchTextField)
    {
        return searchTextField.getText().isEmpty() || !searchTextField.getText().matches("[a-zA-Z]*");
    }

    // gets recipe based on the search bar which calls the recipe api that searches based on the name
    public void getRecipe()
    {

        //List<CardData> results = api.getMealsByFirstLetter('a');
        List<CardData> results = api.searchMealsByName(searchTextField.getText());


        // if the search bar isn't empty
        if (!checkSearchBar(searchTextField))
        {
            if (results.isEmpty())
                resultTextArea.setText("No results found\n");
            else
            {
                resultTextArea.clear();
                for (CardData cardData : results)
                {
                    resultTextArea.setText(resultTextArea.getText() + cardData.getFoodName() + "\n");
                }
            }
        }
        else // if there is nothing in the search bar
        {
            resultTextArea.setText("Search bar is empty or doesn't contain only letters\n");
        }

    }

    // uses api to get & display a random recipe
    public void randomRecipe()
    {
        resultTextArea.clear();
        CardData card = api.getRandomMeal().getFirst();
        resultTextArea.setText(card.getFoodName() + ", food id:" + card.getMealId());
    }


}
