package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.List;

public interface ICookbookRepository {
    void addOrUpdateRecipe(Recipe r);

    void deleteRecipe(Recipe r);

    LiveData<List<Recipe>> getAllRecipes();

    LiveData<List<Recipe>> getAvailableSubRecipes(String parentRecipeName);

    void addMealPlans(List<MealPlan> mealPlans);
}
