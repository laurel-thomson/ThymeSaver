package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeDetailViewModel extends AndroidViewModel {
    private Repository mRepository;
    private String mCurrentRecipeName;
    private Recipe mCurrentRecipe;

    public RecipeDetailViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
    }

    public void setCurrentRecipe(String currentRecipeName) {
        mCurrentRecipeName = currentRecipeName;
        mCurrentRecipe = mRepository.getRecipe(currentRecipeName);
    }

    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public HashMap<Ingredient, Integer> getCurrentRecipeIngredients() {
        return mRepository.getRecipeIngredients(mCurrentRecipe);
    }

    public void updateRecipe() {
        mRepository.addOrUpdateRecipe(mCurrentRecipe);
    }

    public void updateRecipeIngredients(HashMap<Ingredient,Integer> recipeIngredients) {
        for (Map.Entry<Ingredient, Integer> entry : recipeIngredients.entrySet()) {
            mCurrentRecipe.addOrUpdateIngredient(entry.getKey().getName(), entry.getValue());
            updateRecipe();
        }
    }

    public void updateIngredientQuantity(Ingredient i, int quantity) {
        mCurrentRecipe.getRecipeIngredients().put(i.getName(), quantity);
        //todo: only update the quantity in the database, not the whole recipe
        updateRecipe();
    }
}
