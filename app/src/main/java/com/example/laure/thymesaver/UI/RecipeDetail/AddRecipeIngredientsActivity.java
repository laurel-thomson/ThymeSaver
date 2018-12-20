package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.example.laure.thymesaver.UI.BaseAddIngredientsActivity;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class AddRecipeIngredientsActivity extends BaseAddIngredientsActivity {
    public static String RECIPE_NAME = "My recipe name";
    private RecipeDetailViewModel mRecipeDetailViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);
        mAdapter.setSelectedMeasuredIngredients(mRecipeDetailViewModel.getCurrentRecipeIngredients());
    }

    @Override
    public void saveIngredients() {
        mRecipeDetailViewModel.updateRecipeIngredients(mAdapter.getSelectedMeasuredIngredients());
    }
}
