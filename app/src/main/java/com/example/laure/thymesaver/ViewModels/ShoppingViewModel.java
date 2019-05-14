package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository.IPantryRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.IShoppingRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.PantryRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.ShoppingRepository;
import com.example.laure.thymesaver.Models.BulkIngredientState;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.Models.ShoppingListMod;
import com.example.laure.thymesaver.UI.Callbacks.IngredientCallback;

import java.util.HashMap;

public class ShoppingViewModel extends AndroidViewModel {
    private IShoppingRepository mShoppingRepository;
    private IPantryRepository mPantryRepository;

    public ShoppingViewModel(Application application) {
        super(application);
        mShoppingRepository = ShoppingRepository.getInstance();
        mPantryRepository = PantryRepository.getInstance();
    }

    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {
        return mShoppingRepository.getShoppingList();
    }

    public void tryFindIngredient(Ingredient ingredient, IngredientCallback callback) {
         mPantryRepository.getIngredient(ingredient.getName(), callback);
    }

    public void addQuantityToPantry(Ingredient ingredient, int quantity) {
        if (ingredient.isBulk()) {
            ingredient.setQuantity(BulkIngredientState.convertEnumToInt(BulkIngredientState.IN_STOCK));
        }
        else {
            ingredient.setQuantity(ingredient.getQuantity() + quantity);
        }
        mPantryRepository.updateIngredient(ingredient);
    }

    public void addShoppingModification(String name, ModType type, int modifier) {
        mShoppingRepository.addOrUpdateModification(new ShoppingListMod(name, type, modifier));
    }

    public void deleteShoppingListItem(Ingredient ingredient, int quantity) {
        mShoppingRepository.deleteShoppingListItem(ingredient, quantity);
    }

    public void deleteModifier(String name) {
        mShoppingRepository.deleteShoppingModification(name);
    }

    public void refreshShoppingList() { mShoppingRepository.deleteAllModifications(); }
}
