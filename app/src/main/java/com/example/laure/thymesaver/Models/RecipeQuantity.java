package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

public class RecipeQuantity {
    private String unit;
    private double recipeQuantity;

    public RecipeQuantity() {}

    public RecipeQuantity(String unit, double recipeQuantity) {
        this.unit = unit;
        this.recipeQuantity = recipeQuantity;
    }

    public double getRecipeQuantity() {
        return recipeQuantity;
    }

    public void setRecipeQuantity(double recipeQuantity) {
        this.recipeQuantity = recipeQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
