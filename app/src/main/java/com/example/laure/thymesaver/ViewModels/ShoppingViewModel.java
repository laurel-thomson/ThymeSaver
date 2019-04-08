package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.BulkIngredientState;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.Models.ShoppingListMod;

import java.util.HashMap;

public class ShoppingViewModel extends AndroidViewModel {
    private Repository mRepository;

    public ShoppingViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
    }

    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {

        return mRepository.getShoppingList();
    }

    public void addQuantityToPantry(Ingredient ingredient, int quantity) {
        if (ingredient.isBulk()) {
            ingredient.setQuantity(BulkIngredientState.convertEnumToInt(BulkIngredientState.IN_STOCK));
            mRepository.updateIngredient(ingredient);
        }
        else {
            ingredient.setQuantity(ingredient.getQuantity() + 1);
        }
        mRepository.updateIngredient(ingredient);
    }

    public void resetPantryQuantity(Ingredient ingredient, int oldQuantity) {
        ingredient.setQuantity(oldQuantity);
        mRepository.updateIngredient(ingredient);
    }

    public void addShoppingModification(String name, ModType type, int modifier) {
        mRepository.addOrUpdateModification(new ShoppingListMod(name, type, modifier));
    }

    public void deleteShoppingListItem(Ingredient ingredient, int quantity) {
        mRepository.deleteShoppingListItem(ingredient, quantity);
    }

    public void deleteModifier(String name) {
        mRepository.deleteShoppingModification(name);
    }

    public void refreshShoppingList() { mRepository.deleteAllModifications(); }
}
