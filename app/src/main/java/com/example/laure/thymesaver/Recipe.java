package com.example.laure.thymesaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Recipe {
    private String mName;
    private ArrayList<String> mSteps;
    private ArrayList<RecipeIngredient> mIngredients;

    public Recipe(String name) {
        mName = name;
        mSteps = new ArrayList<>();
        mIngredients = new ArrayList<>();
    }

    public void addStep(String step) {
        mSteps.add(step);
    }

    public void removeStep(String step) {
        mSteps.remove(step);
    }

    public List<String> getSteps() {
        return mSteps;
    }

    public void addIngredient(RecipeIngredient i) {
        mIngredients.add(i);
    }

    public void removeIngredient(RecipeIngredient i) {
        mIngredients.remove(i);
    }

    public ArrayList<RecipeIngredient> getIngredients() {
        return mIngredients;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
