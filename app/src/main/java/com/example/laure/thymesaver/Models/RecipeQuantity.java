package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class RecipeQuantity {
    private String unit;
    private int recipeQuantity;

    public RecipeQuantity() {}

    public RecipeQuantity(String unit, int recipeQuantity) {
        this.unit = unit;
        this.recipeQuantity = recipeQuantity;
    }

    public int getRecipeQuantity() {
        return recipeQuantity;
    }

    public void setRecipeQuantity(int recipeQuantity) {
        this.recipeQuantity = recipeQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Exclude
    public String getShortUnitName() {
        switch (unit) {
            case "Quantity":
                return "";
            case "Grams":
                return "g";
            case "Ounces":
                return "oz";
            case "Pounds":
                return "lbs";
            case "Tablespoons":
                return "T";
            case "Teaspoons":
                return "t";
            case "Cups":
                return "c";
            default:
                return "";
        }
    }
}
