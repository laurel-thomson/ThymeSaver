package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.HashMap;
import java.util.List;
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
        mCurrentRecipe = mRepository.getRecipe(currentRecipeName) != null ?
                mRepository.getRecipe(currentRecipeName) :
                new Recipe(currentRecipeName);
    }

    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public HashMap<String, Integer> getRecipeIngredients() {
        return mCurrentRecipe.getRecipeIngredients();
    }

    public List<String> getRecipeSteps() {
        return mCurrentRecipe.getSteps();
    }

    public void updateRecipe() {
        mRepository.addOrUpdateRecipe(mCurrentRecipe);
    }

    public void updateRecipeIngredients(HashMap<String,Integer> recipeIngredients) {
        for (Map.Entry<String, Integer> entry : recipeIngredients.entrySet()) {
            mCurrentRecipe.addOrUpdateIngredient(entry.getKey(), entry.getValue());
            updateRecipe();
        }
    }

    public void updateRecipeIngredientQuantity(String ingredientName, int quantity) {
        mCurrentRecipe.getRecipeIngredients().put(ingredientName, quantity);
        updateRecipe();
    }

    public void addToPantryQuantity(Ingredient i) {
        i.setQuantity(i.getQuantity() + getRecipeQuantity(i));
        mRepository.updateIngredient(i);
    }

    public void removeFromPantryQuantity(Ingredient i) {
        i.setQuantity(Math.max(0, i.getQuantity() - getRecipeQuantity(i)));
        mRepository.updateIngredient(i);
    }

    private int getRecipeQuantity(Ingredient i) {
        int quantity = 0;
        for (String name : mCurrentRecipe.getRecipeIngredients().keySet()) {
            if (name.equals(i.getName())) {
                quantity = mCurrentRecipe.getRecipeIngredients().get(name);
            }
        }
        return quantity;
    }
}
