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
        for (Map.Entry<String, Integer> entry : mAdapter.getMeasuredIngredients().entrySet()) {
            mShoppingViewModel.addShoppingModification(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setAdapterIngredients() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null) return;

                //create a new HashMap that includes all of the ingredients in the pantry
                //the value is 0 if the ingredient is not in the recipe, and nonzero otherwise
                HashMap<String, Integer> totalMeasuredIngredients = new HashMap<>();
                HashMap<String, Integer> shoppingList = mShoppingViewModel.getShoppingList().getValue();
                for (Ingredient i : ingredients) {
                    if (shoppingList.containsKey(i.getName())) {
                        totalMeasuredIngredients.put(i.getName(), shoppingList.get(i.getName()));
                    }
                    else {
                        totalMeasuredIngredients.put(i.getName(), 0);
                    }
                }
                mAdapter.setIngredients(totalMeasuredIngredients);
            }
        });
    }

    @Override
    public void onIngredientCheckedOff(String ingredientName, int quantity) {

    }

    @Override
    public void onDeleteClicked(String ingredientName, int quantity) {
        //do nothing
    }
}
