package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Firebase.Database.Repository;

import java.util.List;

public class CookBookViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<Recipe>> mAllRecipes;

    public CookBookViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public void addRecipe(Recipe r) {
        mRepository.addOrUpdateRecipe(r);
    }

    public void addToMealPlan(Recipe r) {
        //todo: add to meal plan
    }

    public void removeFromMealPlan(Recipe r) {
        //todo: remove from meal plan
    }
}
