package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.UI.BaseAddIngredientsActivity;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;
import java.util.List;

public class AddRecipeIngredientsActivity extends BaseAddIngredientsActivity {
    public static String RECIPE_NAME = "My recipe name";
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private HashMap<Ingredient, Integer> mRecipeIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);
        mRecipeIngredients = mRecipeDetailViewModel.getCurrentRecipeIngredients();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void saveIngredients() {
        mRecipeDetailViewModel.updateRecipeIngredients(mAdapter.getRecipeIngredients());
    }

    @Override
    public void setAdapterIngredients() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null) return;

                //create a new HashMap that includes all of the ingredients in the pantry
                //the value is 0 if the ingredient is not in the recipe, and nonzero otherwise
                HashMap<Ingredient, Integer> measuredIngredients = new HashMap<>();
                for (Ingredient i : ingredients) {
                    if (mRecipeIngredients.containsKey(i)) {
                        measuredIngredients.put(i, mRecipeIngredients.get(i));
                    }
                    else {
                        measuredIngredients.put(i, 0);
                    }
                }
                mAdapter.setIngredients(measuredIngredients);
            }
        });
    }
}
