package org.example.foodrecipeplatform;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class HomeScreenController {

    @FXML
    private TextField searchBar;

    @FXML
    private Text resultText;

    @FXML
    public void initialize() {
        searchBar.setOnKeyPressed(event -> handleKeyPressed(event));
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String query = searchBar.getText();
            if (!query.isEmpty()) {
                fetchNutritionFacts(query);
            }
        }
    }

    private void fetchNutritionFacts(String foodName) {
        try {
            String result = OpenAI.getFoodInfo(foodName);
            resultText.setText(result); // you can later route this to a better UI component
        } catch (Exception e) {
            resultText.setText("Error fetching data.");
            e.printStackTrace();
        }
    }
}
