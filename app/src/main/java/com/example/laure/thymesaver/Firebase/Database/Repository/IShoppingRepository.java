package com.example.laure.thymesaver.Firebase.Database.Repository;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ShoppingListMod;

import java.util.HashMap;

public interface IShoppingRepository {
    void addOrUpdateModification(final ShoppingListMod mod);

    void updateModToChangeIfExists(String name);

    void deleteShoppingModification(final String name);

    void deleteAllModifications();

    void deleteShoppingListItem(final Ingredient ingredient, final int quantity);

    LiveData<HashMap<Ingredient, Integer>> getShoppingList();
}
