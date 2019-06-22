package com.example.laure.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;

import java.util.List;

public interface IPantryRepository {
    void addIngredient(Ingredient i);

    void deleteIngredient(Ingredient i);

    void updateIngredient(Ingredient i);

    void getIngredient(String ingredientName, ValueCallback<Ingredient> callback);

    LiveData<List<Ingredient>> getAllIngredients();
}