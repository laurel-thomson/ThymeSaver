package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.List;

public class PantryViewModel extends AndroidViewModel {
    private Repository mRepository;

    public PantryViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return mRepository.getAllIngredients();
    }

    public void addIngredient(Ingredient i) {
        mRepository.addIngredient(i);
    }

    public void deleteIngredient(Ingredient i) {
        mRepository.deleteIngredient(i);
        //if we're deleting an ingredient, we don't want it to show up in the shopping list
        mRepository.deleteShoppingModification(i.getName());
    }

    public void updateIngredientPantryQuantity(Ingredient i, int quantity) {
        i.setQuantity(quantity);
        mRepository.updateIngredient(i);
    }

    public Ingredient getIngredient(String ingredientName) {
        return mRepository.getIngredient(ingredientName);
    }
}
