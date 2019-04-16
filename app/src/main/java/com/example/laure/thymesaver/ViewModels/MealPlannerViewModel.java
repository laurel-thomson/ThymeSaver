package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.MealPlan;

import java.util.List;

public class MealPlannerViewModel extends AndroidViewModel {
    private Repository mRepository;

    public MealPlannerViewModel(@NonNull Application application) {
        super(application);
        mRepository = Repository.getInstance();
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

    public void cookMealPlan(MealPlan mealPlan) {
        mRepository.removeMealPlanIngredientsFromPantry(mealPlan);
        removeMealPlan(mealPlan);
    }

    public void undoCookMealPlan(MealPlan mealPlan) {
        addMealPlan(mealPlan);
        mRepository.addMealPlanIngredientsToPantry(mealPlan);
    }

    public void removeMealPlan(MealPlan mealPlan) {
        mRepository.deleteMealPlan(mealPlan);
    }
}
