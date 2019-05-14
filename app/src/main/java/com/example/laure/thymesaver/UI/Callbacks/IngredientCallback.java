package com.example.laure.thymesaver.UI.Callbacks;

import com.example.laure.thymesaver.Models.Ingredient;

public interface IngredientCallback {
    void onSuccess(Ingredient ingredient);
    void onError(String error);
}
