package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Firebase.Database.Repository;

import java.util.ArrayList;
import java.util.List;

public class CookBookViewModel extends AndroidViewModel {
    private Repository mRepository;
    LiveData<List<Recipe>> mRecipes;

    public CookBookViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        mRecipes = mRepository.getAllRecipes();
        return mRecipes;
    }

    public void deleteRecipe(Recipe recipe) {
        mRepository.deleteRecipe(recipe);
    }

    public void addRecipesToMealPlan(List<Recipe> recipes, String scheduledDay) {
        List<MealPlan> mealPlans = new ArrayList<>();
        for (Recipe r : recipes) {
            mealPlans.add(new MealPlan(r.getName(), scheduledDay));
        }
        mRepository.addMealPlans(mealPlans);
    }

    public void addRecipe(Recipe recipe) {
        mRepository.addOrUpdateRecipe(recipe);
    }

    public boolean recipeNameExists(String name) {
        List<Recipe> recipes = mRecipes.getValue();
        if (recipes == null) return false;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
