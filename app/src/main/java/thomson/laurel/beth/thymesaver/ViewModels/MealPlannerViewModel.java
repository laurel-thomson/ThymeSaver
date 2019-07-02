package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.IMealPlanRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.MealPlanRepository;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

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

    public void cookMealPlan(MealPlan mealPlan, ValueCallback<HashMap> callback) {
        mRepository.cookMealPlan(mealPlan, callback);
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
