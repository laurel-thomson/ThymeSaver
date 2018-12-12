package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Firebase.Database.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private RecipeRepository mRepository;
    private LiveData<List<Recipe>> mAllRecipes;

    public RecipeViewModel(Application application) {
        super(application);
        mRepository = new RecipeRepository();
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

}
