package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository.IPantryRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.IShoppingRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.PantryRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.ShoppingRepository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;

import java.util.List;

public class PantryViewModel extends AndroidViewModel {
    private IPantryRepository mPantryRepository;
    private IShoppingRepository mShoppingRepository;

    public PantryViewModel(Application application) {
        super(application);
        mPantryRepository = PantryRepository.getInstance();
        mShoppingRepository = ShoppingRepository.getInstance();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return mPantryRepository.getAllIngredients();
    }

    public void addIngredient(Ingredient i) {
        mPantryRepository.addIngredient(i);
    }

    public void updateModToChange(String name) {
        mShoppingRepository.updateModToChangeIfExists(name);
    }

    public void deleteIngredient(Ingredient i) {
        mPantryRepository.deleteIngredient(i);
        //if we're deleting an ingredient, we don't want it to show up in the shopping list
        mShoppingRepository.deleteShoppingModification(i.getName());
    }

    public void updateIngredientPantryQuantity(Ingredient i, int quantity) {
        i.setQuantity(quantity);
        mPantryRepository.updateIngredient(i);
    }

    public void getIngredient(String ingredientName, ValueCallback<Ingredient> callback) {
        mPantryRepository.getIngredient(ingredientName, callback);
    }
}
