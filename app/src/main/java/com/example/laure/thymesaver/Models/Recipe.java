package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Recipe {
    private String name;

    private HashMap<String, Integer> recipeIngredients = new HashMap<>();

    public Recipe() {
        //required empty constructor for Firebase
    }

    public Recipe(String name) {
        this.name = name;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Integer> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void addIngredient(String ingredientName, int quantity) {
        recipeIngredients.put(ingredientName, quantity);
    }
}
