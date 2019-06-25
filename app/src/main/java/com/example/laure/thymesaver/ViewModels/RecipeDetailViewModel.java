package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Database.IRecipeDetailRepository;
import com.example.laure.thymesaver.Database.Firebase.RecipeDetailRepository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;

import java.util.HashMap;

public class RecipeDetailViewModel extends AndroidViewModel {
    private IRecipeDetailRepository mRepository;
    private LiveData<Recipe> mCurrentRecipe;

    public RecipeDetailViewModel(Application application) {
        super(application);
        mRepository = RecipeDetailRepository.getInstance();
    }

    public void setCurrentRecipe(String currentRecipeName) {
        mCurrentRecipe = mRepository.getRecipe(currentRecipeName);
    }

    public LiveData<Recipe> getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void updateRecipe() {
        mRepository.addOrUpdateRecipe(mCurrentRecipe.getValue());
    }

    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients() {
        return mRepository.getRecipeIngredients(mCurrentRecipe.getValue());
    }

    public void addRecipeIngredient(String recipeName, String ingredientName, RecipeQuantity quantity) {
        mRepository.addUpdateRecipeIngredient(recipeName, ingredientName, quantity);
    }

    public void addSubRecipes(String[] subRecipes) {
        Recipe parent = mCurrentRecipe.getValue();
        for (String s : subRecipes) {
            mRepository.addSubRecipe(parent, s);
        }
    }

    public void addSubRecipe(String subRecipe) {
        mRepository.addSubRecipe(mCurrentRecipe.getValue(), subRecipe);
    }

    public void deleteRecipeIngredient(String recipeName, String ingredientName) {
        mRepository.deleteRecipeIngredient(recipeName, ingredientName);
    }

    public void updateSubRecipeIngredient(String subRecipeName, String ingredientName, RecipeQuantity quantity) {
        mRepository.addUpdateRecipeIngredient(subRecipeName, ingredientName, quantity);
    }

    public void removeSubRecipe(String subRecipeName) {
        mRepository.removeSubRecipe(mCurrentRecipe.getValue(), subRecipeName);
    }

}
