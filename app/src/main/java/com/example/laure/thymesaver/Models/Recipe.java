package com.example.laure.thymesaver.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recipe {
    private String name;

    //for Firebase, keys need to be strings
    private HashMap<String, RecipeQuantity> recipeIngredients = new HashMap<>();

    private List<String> steps = new ArrayList<>();

    private String category;

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

    public HashMap<String, RecipeQuantity> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void addOrUpdateIngredient(String ingredientName, RecipeQuantity quantity) {
        recipeIngredients.put(ingredientName, quantity);
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void addStep(String step) {
        steps.add(step);
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
