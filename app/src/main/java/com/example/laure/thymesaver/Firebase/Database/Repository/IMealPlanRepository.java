package com.example.laure.thymesaver.Firebase.Database.Repository;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.UI.Callbacks.HashmapCallback;

import java.util.HashMap;
import java.util.List;

public interface IMealPlanRepository {
    void addMealPlan(MealPlan mealPlan);

    void updateMealPlan(MealPlan mealPlan);

    void deleteMealPlan(MealPlan mealPlan);

    void removeMealPlanIngredientsFromPantry(MealPlan mealPlan, HashmapCallback callback);

    void addMealPlanIngredientsToPantry(HashMap ingredientQuantities);

    LiveData<List<MealPlan>> getMealPlans();
}
