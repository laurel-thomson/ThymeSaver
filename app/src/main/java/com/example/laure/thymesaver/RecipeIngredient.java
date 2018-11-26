package com.example.laure.thymesaver;

public class RecipeIngredient {
    private String mName;
    private String mUnit;
    private double mRecipeQuantity;

    public RecipeIngredient(String name, String unit, double quantity) {
        mName = name;
        mUnit = unit;
        mRecipeQuantity = quantity;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public double getRecipeQuantity() {
        return mRecipeQuantity;
    }

    public void setRecipeQuantity(double recipeQuantity) {
        mRecipeQuantity = recipeQuantity;
    }
}
