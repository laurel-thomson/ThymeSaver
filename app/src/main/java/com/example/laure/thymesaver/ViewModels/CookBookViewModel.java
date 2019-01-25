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
    private LiveData<List<Recipe>> mAllRecipes;

    public CookBookViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
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

    public boolean recipeNameExists(String name) {
        List<Recipe> recipes = mAllRecipes.getValue();
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
