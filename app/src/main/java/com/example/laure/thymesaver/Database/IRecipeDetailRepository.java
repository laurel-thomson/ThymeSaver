package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;

import java.util.HashMap;

public interface IRecipeDetailRepository {
    LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(Recipe r);

    LiveData<Recipe> getRecipe(String recipeName);

    void addOrUpdateRecipe(Recipe r);
}
