package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;
import java.util.List;

public interface IMealPlanRepository {
    void addMealPlan(MealPlan mealPlan);

    void updateMealPlan(MealPlan mealPlan);

    void deleteMealPlan(MealPlan mealPlan);

    void removeMealPlanIngredientsFromPantry(MealPlan mealPlan, ValueCallback<HashMap> callback);

    void addMealPlanIngredientsToPantry(HashMap ingredientQuantities);

    LiveData<List<MealPlan>> getMealPlans();
}
