package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Recipe {
    private String name;

    //for Firebase, keys need to be strings
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

    public void addOrUpdateIngredient(String ingredientName, int quantity) {
        recipeIngredients.put(ingredientName, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Recipe)) {
            return false;
        }

        Recipe r = (Recipe) o;

        return name.equals(r.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode();
    }
}
