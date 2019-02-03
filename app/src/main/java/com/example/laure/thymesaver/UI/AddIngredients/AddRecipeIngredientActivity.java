package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecipeIngredientActivity extends AppCompatActivity {
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

        //todo update this activity
    }
}
