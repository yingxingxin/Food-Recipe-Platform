package org.example.foodrecipeplatform.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.scene.text.Text;
import org.example.foodrecipeplatform.FoodRecipePlatform;
import org.example.foodrecipeplatform.Model.ShoppingItem;
import org.example.foodrecipeplatform.ShoppingList;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ShoppingScreenController implements Initializable {

    @FXML
    private TableView<ShoppingItem> shoppingTable;

    @FXML
    private TableColumn<ShoppingItem, String> ingredientColumn;

    @FXML
    private TableColumn<ShoppingItem, String> quantityColumn;

    @FXML
    private TableColumn<ShoppingItem, Boolean> checkedColumn;

    @FXML
    private Button clearAllButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Text displayUsername;

    private ObservableList<ShoppingItem> shoppingItemsList;
    private ShoppingList shoppingList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        shoppingList = new ShoppingList();
        shoppingItemsList = FXCollections.observableArrayList();

        // Configure the table columns
        ingredientColumn.setCellValueFactory(new PropertyValueFactory<>("ingredientName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checkedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));

        // Make the checkbox column editable
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));
        shoppingTable.setEditable(true);

        // Load the user's display name for the title
        try {
            loadDisplayName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load shopping items from Firebase
        loadShoppingItems();

        // Event handlers for buttons
        clearAllButton.setOnAction(event -> clearAllItems());
        refreshButton.setOnAction(event -> loadShoppingItems());

    }

    private void loadDisplayName() throws ExecutionException, InterruptedException {
        String userId = SessionManager.getUserId();

        DocumentSnapshot snapshot = FoodRecipePlatform.fstore.collection("Users")
                .document(userId)
                .get()
                .get();

        if (snapshot.exists()) {
            String displayName = snapshot.getString("DisplayName");
            if (displayName != null && !displayName.isEmpty()) {
                displayUsername.setText(displayName + "'s Shopping List");
            } else {
                displayUsername.setText("Unknown User");
            }
        } else {
            displayUsername.setText("User not found");
        }
    }

    @FXML
    void back_to_home_Button(ActionEvent event) throws IOException {
        FoodRecipePlatform.setRoot("HomeScreen");
    }

    private void loadShoppingItems() {
        String userId = SessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            showAlert("Error", "No user signed in", "Please sign in to view your shopping list");
            return;
        }

        shoppingItemsList.clear();

        try {
            // Get shopping list items from Firestore
            CollectionReference shoppingListRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId)
                    .collection("ShoppingList");

            ApiFuture<QuerySnapshot> future = shoppingListRef.get();
            QuerySnapshot querySnapshot = future.get();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                String ingredientName = document.getString("ingredientName");
                String quantity = document.getString("quantity");
                Boolean checked = document.getBoolean("checked");

                ShoppingItem item = new ShoppingItem(ingredientName, quantity);
                if (checked != null) {
                    item.setChecked(checked);
                }

                shoppingItemsList.add(item);
            }

            shoppingTable.setItems(shoppingItemsList);

        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Failed to load shopping list", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearAllItems() {
        String userId = SessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            showAlert("Error", "No user signed in", "Please sign in to manage your shopping list");
            return;
        }

        try {
            // Delete all shopping list items from Firestore
            CollectionReference shoppingListRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId)
                    .collection("ShoppingList");

            // Get all documents
            ApiFuture<QuerySnapshot> future = shoppingListRef.get();
            QuerySnapshot querySnapshot = future.get();

            // Delete each document
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                document.getReference().delete();
            }

            shoppingItemsList.clear();
            shoppingTable.setItems(shoppingItemsList);

            showAlert("Success", "Shopping List Cleared", "All items have been removed from your shopping list");

        } catch (InterruptedException | ExecutionException e) {
            showAlert("Error", "Failed to clear shopping list", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateItemCheckedStatus(ShoppingItem item, boolean checked) {
        String userId = SessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        try {
            // Get the document ID for this ingredient
            CollectionReference shoppingListRef = FoodRecipePlatform.fstore
                    .collection("Users")
                    .document(userId)
                    .collection("ShoppingList");

            ApiFuture<QuerySnapshot> future = shoppingListRef
                    .whereEqualTo("ingredientName", item.getIngredientName())
                    .get();

            QuerySnapshot querySnapshot = future.get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                document.getReference().update("checked", checked);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to add ingredients from a recipe by ID
    public void addIngredientsFromMeal(String mealId) {
        shoppingList.addIngredients(mealId);
        loadShoppingItems(); // Refreshes the list after adding
    }
}
