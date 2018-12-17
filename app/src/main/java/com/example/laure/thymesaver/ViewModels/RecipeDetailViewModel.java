package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.List;

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

    public List<Ingredient> getCurrentRecipeIngredients() {
        return mRepository.getRecipeIngredients(mCurrentRecipe);
    }
}
