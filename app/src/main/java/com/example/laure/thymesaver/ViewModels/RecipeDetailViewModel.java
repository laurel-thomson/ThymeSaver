package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.IRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailViewModel extends AndroidViewModel {
    private IRepository mRepository;
    private LiveData<Recipe> mCurrentRecipe;
    private LiveData<HashMap<Ingredient, RecipeQuantity>> mRecipeIngredients;

    public RecipeDetailViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
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

    public void updateRecipeIngredient(String ingredientName, RecipeQuantity quantity) {
        mCurrentRecipe.getValue().getRecipeIngredients().put(ingredientName, quantity);
        updateRecipe();
    }

    public void deleteRecipeIngredient(String ingredientName) {
        Recipe recipe = mCurrentRecipe.getValue();
        recipe.getRecipeIngredients().remove(ingredientName);
        mRepository.addOrUpdateRecipe(recipe);
    }


}
