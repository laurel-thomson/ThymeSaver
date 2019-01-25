package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.UI.AddIngredients.BaseAddIngredientsActivity;
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
        for (Map.Entry<String, Integer> entry : mAdapter.getRecipeIngredients().entrySet()) {
            mRecipe.addOrUpdateIngredient(entry.getKey(), entry.getValue());
        }
        mRecipeDetailViewModel.updateRecipe(mRecipe);
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
                HashMap<String, Integer> recipeIngredients = mRecipe != null
                        ? mRecipe.getRecipeIngredients()
                        : new HashMap<String, Integer>();
                for (Ingredient i : ingredients) {
                    if (recipeIngredients.containsKey(i.getName())) {
                        totalMeasuredIngredients.put(i.getName(), recipeIngredients.get(i.getName()));
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
