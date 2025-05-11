package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.example.foodrecipeplatform.CardData;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.MealDbAPI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @FXML
    public Hyperlink shoppingHyperlink;
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

    // Introduce an ExecutorService to manage background threads
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    /**
     * Helper Method to call Alert
     * @param title -> title of alert
     * @param header -> header of alert
     * @param content -> content body of alert
     */
    private void showAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
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

        // Ensure the executor service is shut down when the application stops
        //Platform.runLater(executorService::shutdownNow);

    }


    @FXML
    void searchButtonClicked(ActionEvent event)
    {
        popNode(searchButton);
        System.out.println("Get Button clicked");
        getRecipe();
    }

    @FXML
    void randomFoodButtonClicked(ActionEvent event)
    {
        popNode(RandomFoodButton);
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

        Platform.runLater(() -> {


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

        });
    }

    // gets recipe based on the search bar which calls the recipe api that searches based on the name
    public void getRecipe()
    {

        String query = searchTextField.getText();
        if (query == null || query.trim().isEmpty()) {
            showAlert("Input Missing", "Please enter a recipe name or a letter.", null);
            return;
        }

        Task<List<CardData>> getRecipeTask = new Task<>() {
            @Override
            protected List<CardData> call() throws Exception {
                // This code runs on a background thread
                if (query.length() == 1) {
                    return api.getMealsByFirstLetter(query.charAt(0));
                } else {
                    return api.searchMealsByName(query);
                }
            }
        };

        getRecipeTask.setOnSucceeded(event -> {
            // This code runs on the JavaFX Application Thread
            List<CardData> results = getRecipeTask.getValue();
            if (results == null || results.isEmpty()) {
                showAlert("No Results", "No recipes found for your search.", null);
                setGrid(new ArrayList<>()); // Clear previous results
            } else {
                setGrid(results); // Update the UI with results
            }
        });

        getRecipeTask.setOnFailed(event -> {
            // This code runs on the JavaFX Application Thread
            Throwable exception = getRecipeTask.getException();
            showAlert("API Error", "Failed to fetch recipes.", exception.getMessage());
            setGrid(new ArrayList<>()); // Clear previous results on error
        });

        executorService.submit(getRecipeTask);


//        List<CardData> results = null; // = api.searchMealsByName(searchTextField.getText());
//
//        if (searchTextField.getText().length() == 1)
//        {
//            results = api.getMealsByFirstLetter(searchTextField.getText().charAt(0));
//        }
//        // uses name search if input is more than one letter
//        else
//        {
//            results = api.searchMealsByName(searchTextField.getText());
//        }
//        setGrid(results); // TESTING
    }

    // uses api to get & display a random recipe
    public void randomRecipe() {
        Task<List<CardData>> randomRecipeTask = new Task<>() {
            @Override
            protected List<CardData> call() throws Exception {
                return api.getRandomMeal();
            }
        };

        randomRecipeTask.setOnSucceeded(event -> {
            // This code runs on the JavaFX Application Thread
            List<CardData> results = randomRecipeTask.getValue();
            if (results == null || results.isEmpty()) {
                showAlert("No Results", "Could not fetch a random recipe.", null);
                setGrid(new ArrayList<>()); // Clear previous results
            } else {
                setGrid(results); // Update the UI with results
            }
        });

        randomRecipeTask.setOnFailed(event -> {
            // This code runs on the JavaFX Application Thread
            Throwable exception = randomRecipeTask.getException();
            showAlert("API Error", "Failed to fetch a random recipe.", exception.getMessage());
            setGrid(new ArrayList<>()); // Clear previous results on error
        });

        executorService.submit(randomRecipeTask);


//        List<CardData> results = api.getRandomMeal();
//        setGrid(results); // TESTING
    }

    public void countryRecipe(String country)
    {

        if (country == null || country.trim().isEmpty() || country.equals("Off")) {
            setGrid(new ArrayList<>()); // Clear results if "Off" is selected or input is invalid
            return;
        }

        Task<List<CardData>> countryRecipeTask = new Task<>() {
            @Override
            protected List<CardData> call() throws Exception {
                // This code runs on a background thread
                System.out.println("Fetching recipes for country: " + country);
                return api.getMealsByCountry(country);
            }
        };

        countryRecipeTask.setOnSucceeded(event -> {
            // This code runs on the JavaFX Application Thread
            List<CardData> results = countryRecipeTask.getValue();
            if (results == null || results.isEmpty()) {
                showAlert("No Results", "No recipes found for the selected country.", null);
                setGrid(new ArrayList<>()); // Clear previous results
            } else {
                setGrid(results); // Update the UI with results
            }
        });

        countryRecipeTask.setOnFailed(event -> {
            // This code runs on the JavaFX Application Thread
            Throwable exception = countryRecipeTask.getException();
            showAlert("API Error", "Failed to fetch recipes by country.", exception.getMessage());
            setGrid(new ArrayList<>()); // Clear previous results on error
        });

        executorService.submit(countryRecipeTask);


//        System.out.println("HERE " + country);
//
//        List<CardData> results = api.getMealsByCountry(country);
//        setGrid(results); // TESTING
    }

    public void ingredientRecipe(String ingredient)
    {

        if (ingredient == null || ingredient.trim().isEmpty()) {
            setGrid(new ArrayList<>()); // Clear results if input is invalid
            return;
        }

        Task<List<CardData>> ingredientRecipeTask = new Task<>() {
            @Override
            protected List<CardData> call() throws Exception {
                // This code runs on a background thread
                System.out.println("Fetching recipes for ingredient: " + ingredient);
                return api.getMealsByIngredient(ingredient);
            }
        };

        ingredientRecipeTask.setOnSucceeded(event -> {
            // This code runs on the JavaFX Application Thread
            List<CardData> results = ingredientRecipeTask.getValue();
            if (results == null || results.isEmpty()) {
                showAlert("No Results", "No recipes found with that ingredient.", null);
                setGrid(new ArrayList<>()); // Clear previous results
            } else {
                setGrid(results); // Update the UI with results
            }
        });

        ingredientRecipeTask.setOnFailed(event -> {
            // This code runs on the JavaFX Application Thread
            Throwable exception = ingredientRecipeTask.getException();
            showAlert("API Error", "Failed to fetch recipes by ingredient.", exception.getMessage());
            setGrid(new ArrayList<>()); // Clear previous results on error
        });

        executorService.submit(ingredientRecipeTask);


//        System.out.println("ingredientRecipe: " + ingredient);
//        List<CardData> results = api.getMealsByIngredient(ingredient);
//
//        setGrid(results); // TESTING
    }

    private void popNode(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

} // End RecipeSearchScreenController class
