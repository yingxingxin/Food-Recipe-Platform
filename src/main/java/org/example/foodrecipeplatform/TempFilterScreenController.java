package org.example.foodrecipeplatform;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class TempFilterScreenController
{
    @FXML
    public Button readFoodButton;

    @FXML
    public SplitMenuButton caloriesSplitMenuButton;

    @FXML
    public TextField ingredientsTextField;

    @FXML
    public TextField countryTextField;

    @FXML
    public TextArea resultsTextArea;

    @FXML
    public MenuItem AscendingMenuItem;

    @FXML
    public MenuItem DescendingMenuItem;

    @FXML
    public MenuItem OffMenuItem;

    public boolean caloriesOn = false;

    @FXML
    void initialize()
    {
        System.out.println("initialized temp filter screen controller");

        AscendingMenuItem.setOnAction((e)-> {
            System.out.println(e.toString() + " selected");
            caloriesSplitMenuButton.setText("calories ↑");
            caloriesOn = true;
        });

        DescendingMenuItem.setOnAction((e)-> {
            System.out.println(e.toString() + " selected");
            caloriesSplitMenuButton.setText("calories ↓");
            caloriesOn = true;
        });

        OffMenuItem.setOnAction((e)-> {
            System.out.println(e.toString() + " selected");
            caloriesSplitMenuButton.setText("calories");
            caloriesOn = false;
        });
    }

    @FXML
    void readFoodButtonClicked(ActionEvent event) throws ExecutionException, InterruptedException {
        System.out.println("readFoodButton clicked");
        getFoodData();
    }


    public void getFoodData()
    {
        // breaking down qureies into
        CollectionReference food = FoodRecipePlatform.fstore.collection("Food");
        Query query = food; // initializing query

        if (countryTextField.getText().isEmpty())
            System.out.println("getFoodData, country: No country selected");
        else
        {
            String[] countryList = countryTextField.getText().split(",");
            System.out.println("getFoodData, country: " + Arrays.toString(countryList));
            query = query.whereIn("country", Arrays.asList(countryList));
        }

        if (ingredientsTextField.getText().isEmpty())
            System.out.println("getFoodData, ingredients: No ingredients selected");
        else
        {
            String[] ingredientList = ingredientsTextField.getText().split(",");
            System.out.println("getFoodData, ingredients: " + Arrays.toString(ingredientList));
            query = query.whereArrayContainsAny("ingredients", Arrays.asList(ingredientList));
        }

        System.out.println("getFoodData, calories: " + caloriesOn + "\n");
        if (caloriesOn)
        {
            if (caloriesSplitMenuButton.getText().contains("↑"))
                query = query.orderBy("calories", Query.Direction.ASCENDING);
            else
                query = query.orderBy("calories", Query.Direction.DESCENDING);
        }

        ApiFuture<QuerySnapshot> querySnapshotList = query.get();

        QuerySnapshot querySnapshot;

        try
        {
            //querySnapshot = query.get();
            querySnapshot = querySnapshotList.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

            resultsTextArea.clear();
            for (QueryDocumentSnapshot document : documents)
            {
                resultsTextArea.setText(resultsTextArea.getText()
                        + "Name: " + document.getString("name") + "\n"
                        + "Country: " + document.getString("country") + "\n"
                        + "Calories: " + document.get("calories") + "\n"
                        + "Description: " + document.getString("description") + "\n"
                        + "Ingredients: " + Objects.requireNonNull(document.get("ingredients")) + "\n"
                        + "Instructions:\n" + document.getString("instructions") + "\n");

            }
            //System.out.println(documents.size());
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }


    public void addfoodButton() throws ExecutionException, InterruptedException {
        // Create a Map to store the data we want to set
//        Map<String, Object> docData = new HashMap<>();
//
//        docData.put("name", "Prime Hydration");
//        docData.put("calories", 321);
//        docData.put("country", "America");
//        docData.put("description", "some American description 1");
//        docData.put("ingredients", Arrays.asList("water", "sugar", "salt", "chicken"));
//        docData.put("instructions",
//                "n/a\n");

        // Add a new document (asynchronously) in collection "cities" with id "LA"
        //ApiFuture<WriteResult> future = FoodRecipePlatform.fstore.collection("Food").document("AmericanFood1").set(docData);

        // future.get() blocks on response
        //System.out.println("Update time : " + future1.get().getUpdateTime());
    }

}
