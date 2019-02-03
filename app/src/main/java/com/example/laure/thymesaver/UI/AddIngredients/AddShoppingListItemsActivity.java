package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddShoppingListItemsActivity extends BaseAddIngredientsActivity {
    private ShoppingViewModel mShoppingViewModel;
    private PantryViewModel mPantryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mShoppingViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void saveIngredients() {
        for (Map.Entry<Ingredient, Integer> entry : mAdapter.getMeasuredIngredients().entrySet()) {
            mShoppingViewModel.addShoppingModification(entry.getKey().getName(), entry.getValue());
        }
    }

    @Override
    public void setAdapterIngredients() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null) return;

                //create a new HashMap that includes all of the ingredients in the pantry
                //the value is 0 if the ingredient is not in the shopping list, and nonzero otherwise
                HashMap<Ingredient, Integer> totalMeasuredIngredients = new HashMap<>();
                HashMap<Ingredient, Integer> shoppingList = mShoppingViewModel.getShoppingList().getValue();
                for (Ingredient i : ingredients) {
                    if (shoppingList.containsKey(i.getName())) {
                        totalMeasuredIngredients.put(i, shoppingList.get(i.getName()));
                    }
                    else {
                        totalMeasuredIngredients.put(i, 0);
                    }
                }
                mAdapter.setIngredients(totalMeasuredIngredients);
            }
        });
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, int quantity) {

    }

    @Override
    public void onIngredientCheckedOff(Ingredient i, int quantity) {

    }

    @Override
    public void onDeleteClicked(Ingredient i, int quantity) {
        //do nothing
    }
}
