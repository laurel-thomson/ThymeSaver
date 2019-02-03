package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecipeIngredientsActivity extends BaseAddIngredientsActivity {
    public static String RECIPE_NAME = "My recipe name";
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private PantryViewModel mPantryViewModel;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        super.onCreate(savedInstanceState);
        mRecipeDetailViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                mRecipe = recipe;
            }
        });
    }

    @Override
    public void saveIngredients() {
        if (mRecipe == null) {
            mRecipe = new Recipe(mRecipeDetailViewModel.getCurrentRecipeName());
        }
        for (Map.Entry<Ingredient, Integer> entry : mAdapter.getMeasuredIngredients().entrySet()) {
            mRecipe.addOrUpdateIngredient(entry.getKey().getName(), entry.getValue());
        }
        mRecipeDetailViewModel.updateRecipe(mRecipe);
    }

    @Override
    public void setAdapterIngredients() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null) return;

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

    }
}
