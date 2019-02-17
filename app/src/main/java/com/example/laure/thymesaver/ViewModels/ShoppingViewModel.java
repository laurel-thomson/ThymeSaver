package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.BulkIngredientStates;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ShoppingListMod;

import java.util.HashMap;
import java.util.List;

public class ShoppingViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<HashMap<Ingredient, Integer>> mShoppingList;

    public ShoppingViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mShoppingList = mRepository.getShoppingList();
    }

    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {
        return mShoppingList;
    }

    public void addQuantityToPantry(Ingredient ingredient, int quantity) {
        if (ingredient.isBulk()) {
            ingredient.setQuantity(BulkIngredientStates.convertEnumToInt(BulkIngredientStates.IN_STOCK));
            mRepository.updateIngredient(ingredient);
        }
        else {
            mRepository.addQuantityToIngredient(ingredient.getName(), quantity);
        }
    }

    public void removeQuantityFromPantry(String ingredientName, int quantity) {
        mRepository.removeQuantityFromIngredient(ingredientName, quantity);
    }

    public void addShoppingModification(String name, int modifier) {
        mRepository.addShoppingModification(new ShoppingListMod(name, modifier));
    }

    public void deleteModifier(String name) {
        mRepository.deleteShoppingModification(name);
    }
}
