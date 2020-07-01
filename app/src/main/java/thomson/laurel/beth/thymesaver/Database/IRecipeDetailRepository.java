package thomson.laurel.beth.thymesaver.Database;

import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;
import java.util.List;

public interface IRecipeDetailRepository {
    LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(String recipeName);

    LiveData<Recipe> getRecipe(String recipeName);

    void getRecipe(String recipeName, ValueCallback<Recipe> callback);

    void addOrUpdateRecipe(Recipe r);

    void renameRecipe(Recipe r, String newName, Callback callback);

    void updateRecipeSteps(String recipeName, List<Step> steps);

    void addSubRecipe(Recipe parent, String childName);

    void removeSubRecipe(Recipe parent, String childName);

    void addUpdateRecipeIngredient(String recipeName, String ingredientName, RecipeQuantity quantity);

    void deleteRecipeIngredient(String recipeName, String ingredientName);

    void clearAllChecks(String recipeName);

    void updateCategories(String recipeName, List<String> categories);
}
