package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.UI.AddIngredients.BaseAddIngredientsActivity;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;
import java.util.List;

public class AddRecipeIngredientsActivity extends BaseAddIngredientsActivity {
    public static String RECIPE_NAME = "My recipe name";
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private PantryViewModel mPantryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRecipeDetailViewModel = ViewModelProviders.of(getParent().getApplicationContext()).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
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
                HashMap<String, Integer> totalMeasuredIngredients = new HashMap<>();
                HashMap<String, Integer> recipeIngredients = mRecipeDetailViewModel.getRecipeIngredients();
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
}
