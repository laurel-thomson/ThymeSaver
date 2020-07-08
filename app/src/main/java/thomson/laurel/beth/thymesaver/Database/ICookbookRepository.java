package thomson.laurel.beth.thymesaver.Database;

import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.List;

public interface ICookbookRepository {
    void addOrUpdateRecipe(Recipe r);

    void addRecipe(Recipe r, Callback callback);

    void deleteRecipe(Recipe r);

    LiveData<List<Recipe>> getAllRecipes();

    void getAllRecipes(ValueCallback<List<Recipe>> callback);

    LiveData<List<Recipe>> getAvailableSubRecipes(String parentRecipeName);

    void addMealPlans(List<MealPlan> mealPlans);

    void getAllRecipeCategories(ValueCallback<List<String>> callback);
}
