package org.example.foodrecipeplatform;

/**
 * Card Data Class
 */
public class CardData {

    // Privater Member Variables
    private String foodName;
    private String description;
    private String imageURL;
    private String mealId;

    // Parametrized Constructor
    public CardData(String foodName, String description, String imageURL) {
        this.foodName = foodName;
        this.description = description;
        this.imageURL = imageURL;
    }

    // Getter and Setter Methods
    public String getFoodName() {
        return foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getMealId() { return mealId; }
    public void setMealId(String mealId) { this.mealId = mealId; }

}   // End Card Data class