package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * RecipeSearchScreenController -> Class to search for recipes within the food API DB
 */
public class RecipeSearchScreenController
{
    @FXML
    public Button searchButton;
    @FXML
    public Button RandomFoodButton;
    @FXML
    private ImageView Profile_photo;
    @FXML
    public ComboBox<String> CountryComboBox;
    @FXML
    public TextField searchTextField;
//    @FXML
//    public TextField IngredientTextField;
//    @FXML
//    public Hyperlink shoppingHyperlink;
    @FXML
    public Hyperlink HomePageHyperlink;
    @FXML
    public TextField ingredientTextField;
    @FXML
    public ListView<String> ingredientListView;
    @FXML
    public ScrollPane resultScrollPlain;
    @FXML
    public GridPane resultGridPlain;
    @FXML
    private Hyperlink DisplayUserName;


    List<CardData> cards = new ArrayList<>();

    private String profilePictureUrl ;

    MealDbAPI api;

    /**
     * Helper Method to call Alert
     * @param title -> title of alert
     * @param header -> header of alert
     * @param content -> content body of alert
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    } // End showAlert method

    /**
     * loadDisplayName -> method to load the Display name in DB to its place on the screen upon init
     */
    void loadDisplayName() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        DocumentSnapshot snapshot = FoodRecipePlatform.fstore.collection("Users")
                .document(userId)
                .get()
                .get();

        if (snapshot.exists()) {
            String displayName = snapshot.getString("DisplayName");
            if (displayName != null && !displayName.isEmpty()) {
                DisplayUserName.setText(displayName); // Works with javafx.scene.text.Text -> .setText() method
            } else {
                DisplayUserName.setText("Unknown User");
            }
        } else {
            DisplayUserName.setText("User not found");
        }
    } // End loadDisplayName method


    /**
     * getImage_DB ->  method to get profile pic from db -> image is set on profile page screen
     */
    void getImage_DB() {
        String userId = SessionManager.getUserId();
        System.out.println("User ID in getImage_DB: " + userId); // Debug

        profilePictureUrl = null;

        try {
            DocumentReference userDocRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId);

            ApiFuture<DocumentSnapshot> future = userDocRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                String photoUrl = document.getString("ProfilePicture");
                System.out.println("Retrieved photoUrl from Firebase: " + photoUrl); // Debug

                if (photoUrl != null && !photoUrl.isEmpty()) {
                    profilePictureUrl = photoUrl;
                    try {
                        Image image = new Image(profilePictureUrl);
                        Profile_photo.setImage(image);
                        System.out.println("Image set successfully.");
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error creating Image: " + e.getMessage());
                        showAlert("Error", "Invalid Profile Picture URL", "The URL retrieved is not valid.");
                    }
                } else {
                    showAlert("Warning", "Profile Picture URL is empty or missing.", null);
                }
            } else {
                showAlert("Error", "User document not found.", null);
            }
        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Failed to load profile picture URL from database.", e.getMessage());
            e.printStackTrace();
        }
    } // End getImage_DB method


    @FXML
    void initialize() throws ExecutionException, InterruptedException // clean up initialize method by putting stuff below into a method
    {
        // Load profile photo/display name -> from DB
        getImage_DB();
        loadDisplayName();

        // initialize new API-DB
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

        //System.out.println(CountryComboBox.getSelectionModel().getSelectedItem());

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
                ("Chicken", "Salmon", "Beef", "Avocado", "Pork", "Asparagus", "Bread", "Broccoli"
                , "Carrots", "Bacon", "Aubergine", "Lamb", "Kale", "Lettuce", "Lime");

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
    void homePageHyperlinkClicked(ActionEvent event) throws IOException
    {
        FoodRecipePlatform.setRoot("HomeScreen");
    }

    @FXML
    void setHomePageHyperlinkClicked(ActionEvent event) throws IOException
    {
        FoodRecipePlatform.setRoot("ShoppingScreen");
    }

    @FXML
    void profileHyperlink(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("ProfilePage");
    } // End OpenProfileScreen

    @FXML
    private void setGrid(List<CardData> inputCardList) {
        // testing grid
        resultGridPlain.getChildren().clear();
        //cards.addAll(getData());
        cards = inputCardList;

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
                resultGridPlain.add(anchorPane, col++, row);
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

    // gets recipe based on the search bar which calls the recipe api that searches based on the name
    public void getRecipe()
    {
        List<CardData> results = null; // = api.searchMealsByName(searchTextField.getText());

        if (searchTextField.getText().length() == 1)
        {
            results = api.getMealsByFirstLetter(searchTextField.getText().charAt(0));
        }
        // uses name search if input is more than one letter
        else
        {
            results = api.searchMealsByName(searchTextField.getText());
        }
        setGrid(results); // TESTING
    }

    // uses api to get & display a random recipe
    public void randomRecipe()
    {
        List<CardData> results = api.getRandomMeal();
        setGrid(results); // TESTING
    }

    public void countryRecipe(String country)
    {
        System.out.println("HERE " + country);

        List<CardData> results = api.getMealsByCountry(country);
        setGrid(results); // TESTING
    }

    public void ingredientRecipe(String ingredient)
    {
        System.out.println("ingredientRecipe: " + ingredient);
        List<CardData> results = api.getMealsByIngredient(ingredient);

        setGrid(results); // TESTING
    }
} // End RecipeSearchScreenController class
