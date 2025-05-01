package org.example.foodrecipeplatform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class CardDataTest {
    CardData cardData;

    @BeforeEach
    void setUp() {
        cardData = new CardData("name1", "description1", "img1");
        cardData.setMealId("idMeal1");
    }

    // testing getFoodName
    @Test
    void getFoodName() {
        assertEquals("name1", cardData.getFoodName());
    }

    @Test
    void getFoodName_WithDifferentName() {
        cardData.setFoodName("!@#$%^&*()@");
        assertEquals("!@#$%^&*()@", cardData.getFoodName());
    }

    @Test
    void getFoodName_WithEmptyName() {
        cardData.setFoodName("");
        assertEquals("", cardData.getFoodName());
    }

    // Testing getDescription
    @Test
    void getDescription() {
        assertEquals("description1", cardData.getDescription());
    }

    @Test
    void getDifferentDescription() {
        cardData.setDescription("!@#$%^&*()@");
        assertEquals("!@#$%^&*()@", cardData.getDescription());
    }

    @Test
    void getEmptyDescription() {
        cardData.setDescription("");
        assertEquals("", cardData.getDescription());
    }

    // Testing getMealId
    @Test
    void getMealId() {
        assertEquals("idMeal1", cardData.getMealId());
    }

    @Test
    void getNullMealId() {
        cardData.setMealId(null);
        assertNull(cardData.getMealId());
    }

    @Test
    void getAnotherMealId() {
        cardData.setMealId("!@#$%^&*()@");
        assertEquals("!@#$%^&*()@", cardData.getMealId());
    }
}