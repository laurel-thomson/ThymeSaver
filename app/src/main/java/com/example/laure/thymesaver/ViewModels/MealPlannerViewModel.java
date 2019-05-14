package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.Repository.IMealPlanRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.MealPlanRepository;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;
import java.util.List;

public class MealPlannerViewModel extends AndroidViewModel {
    private IMealPlanRepository mRepository;

    public MealPlannerViewModel(@NonNull Application application) {
        super(application);
        mRepository = MealPlanRepository.getInstance();
    }

    public LiveData<List<MealPlan>> getMealPlans() {
        return mRepository.getMealPlans();
    }

    public void addMealPlan(MealPlan mealPlan) {
        mRepository.addMealPlan(mealPlan);
    }

    public void updateMealPlan(MealPlan mealPlan) {
        mRepository.updateMealPlan(mealPlan);
    }

    public void cookMealPlan(MealPlan mealPlan, ValueCallback callback) {
        mRepository.removeMealPlanIngredientsFromPantry(mealPlan, callback);
        removeMealPlan(mealPlan);
    }

    public void undoCookMealPlan(MealPlan mealPlan, HashMap ingredientQuantities) {
        addMealPlan(mealPlan);
        mRepository.addMealPlanIngredientsToPantry(ingredientQuantities);
    }

    public void removeMealPlan(MealPlan mealPlan) {
        mRepository.deleteMealPlan(mealPlan);
    }
}
