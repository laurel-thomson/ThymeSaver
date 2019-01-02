package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.List;

public class MealPlannerViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<MealPlan>> mMealPlans;

    public MealPlannerViewModel(@NonNull Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mMealPlans = mRepository.getMealPlans();
    }

    public LiveData<List<MealPlan>> getMealPlans() {
        return mMealPlans;
    }

    public void addMealPlan(MealPlan mealPlan) {
        mRepository.addMealPlan(mealPlan);
    }

    public void updateMealPlan(MealPlan mealPlan) {
        mRepository.updateMealPlan(mealPlan);
    }

    public void mealPlanCookChanged(MealPlan mealPlan) {
        updateMealPlan(mealPlan);
        if (mealPlan.isCooked()) {
            mRepository.removeMealPlanIngredientsFromPantry(mealPlan);
        }
        else {
            mRepository.addMealPlanIngredientsToPantry(mealPlan);
        }
    }

    public void removeMealPlan(MealPlan mealPlan) {
        mRepository.removeMealPlan(mealPlan);
    }
}
