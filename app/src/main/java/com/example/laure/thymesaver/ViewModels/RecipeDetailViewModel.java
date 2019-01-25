package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailViewModel extends AndroidViewModel {
    private Repository mRepository;
    private String mCurrentRecipeName;
    private LiveData<Recipe> mCurrentRecipe;

    public RecipeDetailViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
    }

    public void setCurrentRecipe(String currentRecipeName) {
        mCurrentRecipeName = currentRecipeName;
        mCurrentRecipe = mRepository.getRecipe(currentRecipeName);
    }

    public LiveData<Recipe> getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public String getCurrentRecipeName() { return mCurrentRecipeName; }

    public void updateRecipe() {
        mRepository.addOrUpdateRecipe(mCurrentRecipe.getValue());
    }

    public void updateRecipe(Recipe recipe) {
        mRepository.addOrUpdateRecipe(recipe);
    }

    public void updateRecipeIngredientQuantity(String ingredientName, int quantity) {
        mCurrentRecipe.getValue().getRecipeIngredients().put(ingredientName, quantity);
        updateRecipe();
    }

    public void deleteRecipeIngredient(String ingredientName) {
        Recipe recipe = mCurrentRecipe.getValue();
        recipe.getRecipeIngredients().remove(ingredientName);
        mRepository.addOrUpdateRecipe(recipe);
    }
}
