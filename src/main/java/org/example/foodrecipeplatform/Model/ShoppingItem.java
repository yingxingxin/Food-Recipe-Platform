package org.example.foodrecipeplatform.Model;

import java.util.HashMap;
import java.util.Map;

public class ShoppingItem {
    private String ingredientName;
    private String quantity;
    private boolean checked;


    public ShoppingItem(String ingredientName, String quantity) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.checked = false;
    }

    // Getters and setters
    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return quantity + " " + ingredientName;
    }

}
