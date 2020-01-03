package thomson.laurel.beth.thymesaver.Database;

import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.List;

public interface IPantryRepository {
    void addIngredient(Ingredient i);

    void deleteIngredient(Ingredient i);

    void updateIngredient(Ingredient i);

    void getIngredient(String ingredientName, ValueCallback<Ingredient> callback);

    LiveData<List<Ingredient>> getAllIngredients();
}
