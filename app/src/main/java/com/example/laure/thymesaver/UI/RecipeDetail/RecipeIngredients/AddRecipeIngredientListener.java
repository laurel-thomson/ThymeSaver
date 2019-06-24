package com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.RecipeQuantity;

public interface AddRecipeIngredientListener {
    void onIngredientAddedOrUpdated(Ingredient ing, RecipeQuantity quantity);
    void onDeleteClicked(Ingredient i, RecipeQuantity quantity);
    void onIngredientClicked(Ingredient i, RecipeQuantity quantity);
    void onIngredientLongClicked(Ingredient i);
}
