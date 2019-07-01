package thomson.laurel.beth.thymesaver.Database;

import android.arch.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ShoppingListMod;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;

public interface IShoppingRepository {
    void addOrUpdateModification(final ShoppingListMod mod);

    void updateModToChangeIfExists(String name);

    void deleteShoppingModification(final String name);

    void deleteAllModifications();

    void deleteShoppingListItem(final Ingredient ingredient, final int quantity);

    LiveData<HashMap<Ingredient, Integer>> getShoppingList();

    void getShoppingList(ValueCallback<HashMap<Ingredient, Integer>> callback);
}
