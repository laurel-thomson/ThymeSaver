package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.Models.ShoppingListMod;
import com.example.laure.thymesaver.UI.Callback;

import java.util.HashMap;
import java.util.List;

public interface IRepository {
    void initializePreferredPantry(Callback callback);

    void updatePreferredPantry(String pantryId);

    LiveData<List<Recipe>> getAllRecipes();

    LiveData<List<Ingredient>> getAllIngredients();

    LiveData<HashMap<Ingredient, Integer>> getShoppingList();

    LiveData<List<MealPlan>> getMealPlans();

    LiveData<List<Pantry>> getPantries();

    LiveData<List<PantryRequest>> getPantryRequests();

    LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(Recipe r);

    LiveData<Recipe> getRecipe(String recipeName);

    void trySendJoinPantryRequest(String email, Callback callBack);

    void acceptJoinRequest(PantryRequest request);

    void declineJoinRequest(PantryRequest request);

    String getPreferredPantryId();

    void addOrUpdateRecipe(Recipe r);

    void deleteRecipe(Recipe r);

    void addIngredient(Ingredient i);

    void deleteIngredient(Ingredient i);

    void updateIngredient(Ingredient i);

    Ingredient getIngredient(String ingredientName);

    void addMealPlan(MealPlan mealPlan);

    void addMealPlans(List<MealPlan> mealPlans);

    void updateMealPlan(MealPlan mealPlan);

    void deleteMealPlan(MealPlan mealPlan);

    void removeMealPlanIngredientsFromPantry(MealPlan mealPlan);

    void addMealPlanIngredientsToPantry(MealPlan mealPlan);

    void addOrUpdateModification(final ShoppingListMod mod);

    void updateModToChangeIfExists(String name);

    void deleteShoppingModification(final String name);

    void deleteAllModifications();

    void deleteShoppingListItem(final Ingredient ingredient, final int quantity);
}
