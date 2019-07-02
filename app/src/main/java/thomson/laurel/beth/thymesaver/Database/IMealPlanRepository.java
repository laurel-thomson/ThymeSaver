package thomson.laurel.beth.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;
import java.util.List;

public interface IMealPlanRepository {
    void addMealPlan(MealPlan mealPlan);

    void updateMealPlan(MealPlan mealPlan);

    void deleteMealPlan(MealPlan mealPlan);

    void cookMealPlan(MealPlan mealPlan, ValueCallback<HashMap> callback);

    void addMealPlanIngredientsToPantry(HashMap ingredientQuantities);

    LiveData<List<MealPlan>> getMealPlans();
}
