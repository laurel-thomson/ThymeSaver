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
    private LiveData<HashMap<Ingredient, RecipeQuantity>> mRecipeIngredients;

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

    public void addRecipeIngredient(String ingredientName, RecipeQuantity quantity) {
        mCurrentRecipe.getValue().getRecipeIngredients().put(ingredientName, quantity);
        updateRecipe();
    }

    public void addSubRecipes(String[] subRecipes) {
        Recipe parentRecipe = mCurrentRecipe.getValue();
        for (String s : subRecipes) {
           mRepository.addSubRecipe(parentRecipe, s);
        }
    }

    public void deleteRecipeIngredient(String ingredientName) {
        Recipe recipe = mCurrentRecipe.getValue();
        recipe.getRecipeIngredients().remove(ingredientName);
        mRepository.addOrUpdateRecipe(recipe);
    }


}
