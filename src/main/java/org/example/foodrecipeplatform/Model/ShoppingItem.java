package org.example.foodrecipeplatform.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class ShoppingItem {
    private StringProperty ingredientName;
    private StringProperty quantity;
    private BooleanProperty checked;
    private String documentId;

    public ShoppingItem(String ingredientName, String quantity) {
        this.ingredientName = new SimpleStringProperty(ingredientName);
        this.quantity = new SimpleStringProperty(quantity);
        this.checked = new SimpleBooleanProperty(false);
    }

    // Getters and setters
    public String getIngredientName() {
        return ingredientName.get();
    }

    public StringProperty ingredientNameProperty() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName.set(ingredientName);
    }

    public String getQuantity() {
        return quantity.get();
    }

    public StringProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity.set(quantity);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean checkedValue) {
        this.checked.set(checkedValue);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return quantity + " " + ingredientName;
    }

}
