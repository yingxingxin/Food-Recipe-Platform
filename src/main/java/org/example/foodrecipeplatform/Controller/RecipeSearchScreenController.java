package org.example.foodrecipeplatform.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    public Button IngredientButton;
    @FXML
    public ComboBox<String> CountryComboBox;

    @FXML
    public TextArea resultTextArea;
    @FXML
    public TextField searchTextField;
    @FXML
    public TextField IngredientTextField;

    @FXML
    public Hyperlink shoppingHyperlink;
    @FXML
    public Hyperlink HomePageHyperlink;
    @FXML
    public Hyperlink profileHyperlink;

    @FXML
    public TextField ingredientTextField;
    @FXML
    public ListView<String> ingredientListView;


    MealDbAPI api;

    @FXML
    void initialize()
    {
        api = new MealDbAPI();

        // adding countries to the country combobox button
        // selecting off will inactivate it
        String[] countries = { "Off",
                "American", "British", "Canadian", "Chinese", "Croatian", "Dutch", "Egyptian",
                "Filipino", "French", "Greek", "Indian", "Irish", "Italian", "Jamaican", "Japanese", "Kenyan",
                "Malaysian", "Mexican", "Moroccan", "Polish", "Portuguese", "Russian", "Spanish", "Thai", "Tunisian",
                "Turkish", "Ukrainian", "Uruguayan", "Vietnamese"
        };
        CountryComboBox.getItems().addAll(countries);

        System.out.println(CountryComboBox.getSelectionModel().getSelectedItem());

        CountryComboBox.setOnAction(event ->
        {
            String country = CountryComboBox.getSelectionModel().getSelectedItem();
            if (country.equals("Off"))
            {
                System.out.println("Selected: " + country);
            }
            // only search recipe by country if the selection picked isn't Off
            else
            {
                countryRecipe(country);
                System.out.println("Selected: " + country);
            }

        });


        // creating list view for showing ingredients
        ObservableList<String> ingredientsList = FXCollections.observableArrayList
                ("Chicken", "Salmon", "Beef", "Avocado", "Pork", "Asparagus", "Bread", "Broccoli");

        FilteredList<String> filtered = new FilteredList<>(ingredientsList, s -> true);

        ingredientTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            filtered.setPredicate(item -> item.toLowerCase().contains(newVal.toLowerCase()));
        });

        ingredientListView.setItems(filtered);

        ingredientListView.setOnMouseClicked(e -> {
            String selected = ingredientListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                System.out.println("Selected: " + selected);

                // Do something with it (e.g. update a label or pass to another scene)
                ingredientRecipe(selected);
            }
        });

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

    @FXML
    void IngredientButtonClicked(ActionEvent event)
    {
        System.out.println("Ingredient Button Clicked");
        ingredientRecipe(IngredientTextField.getText());
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
        List<CardData> results = null; // = api.searchMealsByName(searchTextField.getText());

        // checks if input is correct
        if (!checkSearchBar(searchTextField))
        {
            resultTextArea.clear();

            // uses the first letter search from api if the input is one letter
            if (searchTextField.getText().length() == 1)
            {
                results = api.getMealsByFirstLetter(searchTextField.getText().charAt(0));
            }
            // uses name search if input is more than one letter
            else
            {
                results = api.searchMealsByName(searchTextField.getText());
            }

            for (CardData cardData : results)
            {
                resultTextArea.setText(resultTextArea.getText() + cardData.getFoodName() + "\n");
            }

        }
        else // if input for search bar is incorrect: empty or numbers
        {
            resultTextArea.setText("Error: Search bar is empty or input contain non-letter values..\n");
        }

    }

    // uses api to get & display a random recipe
    public void randomRecipe()
    {
        resultTextArea.clear();
        CardData card = api.getRandomMeal().getFirst();
        resultTextArea.setText(card.getFoodName() + ", food id:" + card.getMealId());
    }

    public void countryRecipe(String country)
    {
        System.out.println("HERE " + country);

        List<CardData> results = api.getMealsByCountry(country);

        resultTextArea.clear();
        for (CardData cardData : results)
        {
            resultTextArea.setText(resultTextArea.getText() + cardData.getFoodName() + "\n");
        }
    }

    public void ingredientRecipe(String ingredient)
    {
        System.out.println("ingredientRecipe: " + ingredient);
        List<CardData> results = api.getMealsByIngredient(ingredient);

        resultTextArea.clear();
        for (CardData cardData : results)
        {
            resultTextArea.setText(resultTextArea.getText() + cardData.getFoodName() + "\n");
        }
    }
}
