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
        mCurrentRecipe = mRepository.getRecipe(currentRecipeName);
    }

    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public HashMap<Ingredient, Integer> getRecipeIngredients() {
        return mRepository.getRecipeIngredients(mCurrentRecipe);
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

    public void updateRecipeIngredientQuantity(Ingredient i, int quantity) {
        mCurrentRecipe.getRecipeIngredients().put(i.getName(), quantity);
        //todo: only update the quantity in the database, not the whole recipe
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
