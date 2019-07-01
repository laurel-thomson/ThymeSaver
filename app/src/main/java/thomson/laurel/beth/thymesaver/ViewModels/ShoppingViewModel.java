package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Database.IPantryRepository;
import thomson.laurel.beth.thymesaver.Database.IShoppingRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.PantryRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.ShoppingRepository;
import thomson.laurel.beth.thymesaver.Models.BulkIngredientState;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ModType;
import thomson.laurel.beth.thymesaver.Models.ShoppingListMod;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;

public class ShoppingViewModel extends AndroidViewModel {
    private IShoppingRepository mShoppingRepository;
    private IPantryRepository mPantryRepository;

    public ShoppingViewModel(Application application) {
        super(application);
        mShoppingRepository = ShoppingRepository.getInstance();
        mPantryRepository = PantryRepository.getInstance();
    }

    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {
        return mShoppingRepository.getShoppingList();
    }

    public void getShoppingList(ValueCallback<HashMap<Ingredient, Integer>> callback) {
        mShoppingRepository.getShoppingList(callback);
    }

    public void tryFindIngredient(Ingredient ingredient, ValueCallback<Ingredient> callback) {
         mPantryRepository.getIngredient(ingredient.getName(), callback);
    }

    public void addQuantityToPantry(Ingredient ingredient, int quantity) {
        if (ingredient.isBulk()) {
            ingredient.setQuantity(BulkIngredientState.convertEnumToInt(BulkIngredientState.IN_STOCK));
        }
        else {
            ingredient.setQuantity(ingredient.getQuantity() + quantity);
        }
        mPantryRepository.updateIngredient(ingredient);
    }

    public void addShoppingModification(String name, ModType type, int modifier) {
        mShoppingRepository.addOrUpdateModification(new ShoppingListMod(name, type, modifier));
    }

    public void deleteShoppingListItem(Ingredient ingredient, int quantity) {
        mShoppingRepository.deleteShoppingListItem(ingredient, quantity);
    }

    public void deleteModifier(String name) {
        mShoppingRepository.deleteShoppingModification(name);
    }

    public void refreshShoppingList() { mShoppingRepository.deleteAllModifications(); }
}
