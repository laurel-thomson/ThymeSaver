package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Database.IPantryRepository;
import thomson.laurel.beth.thymesaver.Database.IShoppingRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.PantryRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.ShoppingRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.List;

public class PantryViewModel extends AndroidViewModel {
    private IPantryRepository mPantryRepository;
    private IShoppingRepository mShoppingRepository;

    public PantryViewModel(Application application) {
        super(application);
        mPantryRepository = PantryRepository.getInstance();
        mShoppingRepository = ShoppingRepository.getInstance();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return mPantryRepository.getAllIngredients();
    }

    public void addIngredient(Ingredient i) {
        mPantryRepository.addIngredient(i);
    }

    public void updateModToChange(String name) {
        mShoppingRepository.updateModToChangeIfExists(name);
    }

    public void deleteIngredient(Ingredient i) {
        mPantryRepository.deleteIngredient(i);
        //if we're deleting an ingredient, we don't want it to show up in the shopping list
        mShoppingRepository.deleteShoppingModification(i.getName());
    }

    public void updateIngredientPantryQuantity(Ingredient i, int quantity) {
        i.setQuantity(quantity);
        mPantryRepository.updateIngredient(i);
    }

    public void getIngredient(String ingredientName, ValueCallback<Ingredient> callback) {
        mPantryRepository.getIngredient(ingredientName, callback);
    }
}
