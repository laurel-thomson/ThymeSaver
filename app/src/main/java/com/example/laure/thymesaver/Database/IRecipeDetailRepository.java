package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;

public interface IRecipeDetailRepository {
    LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(String recipeName);

    LiveData<Recipe> getRecipe(String recipeName);

    void getRecipe(String recipeName, ValueCallback<Recipe> callback);

    void addOrUpdateRecipe(Recipe r);

    void addSubRecipe(Recipe parent, String childName);

    void removeSubRecipe(Recipe parent, String childName);

    void addUpdateRecipeIngredient(String recipeName, String ingredientName, RecipeQuantity quantity);

    void deleteRecipeIngredient(String recipeName, String ingredientName);

    void clearAllChecks(String recipeName);
}
