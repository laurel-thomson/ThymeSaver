package com.example.laure.thymesaver.UI.AddIngredients;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.RecipeQuantity;

public interface AddShoppingListItemListener {
    void onIngredientAdded(Ingredient ing, int quantity);
}
