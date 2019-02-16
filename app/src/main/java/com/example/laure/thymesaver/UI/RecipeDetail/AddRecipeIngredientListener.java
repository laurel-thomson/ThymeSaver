package com.example.laure.thymesaver.UI.RecipeDetail;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.RecipeQuantity;

public interface AddRecipeIngredientListener {
    void onIngredientAdded(Ingredient ing, RecipeQuantity quantity);
}
