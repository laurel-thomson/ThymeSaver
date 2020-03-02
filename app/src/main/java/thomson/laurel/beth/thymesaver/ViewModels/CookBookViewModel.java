package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Database.Firebase.CookbookRepository;
import thomson.laurel.beth.thymesaver.Database.ICookbookRepository;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.ArrayList;
import java.util.List;

public class CookBookViewModel extends AndroidViewModel {
    private ICookbookRepository mRepository;
    private List<Recipe> mRecipes;
    private LiveData<List<Recipe>> mRecipesLiveData;

    public CookBookViewModel(Application application) {
        super(application);
        mRepository = CookbookRepository.getInstance();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        mRecipesLiveData = mRepository.getAllRecipes();
        return mRecipesLiveData;
    }

    public void getAllRecipes(ValueCallback<List<Recipe>> callback) {
        mRepository.getAllRecipes(new ValueCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> value) {
                mRecipes = value;
                callback.onSuccess(value);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public LiveData<List<Recipe>> getAvailableSubRecipes(String parentRecipeName) {
        return mRepository.getAvailableSubRecipes(parentRecipeName);
    }

    public void deleteRecipe(Recipe recipe) {
        mRepository.deleteRecipe(recipe);
    }

    public void addRecipesToMealPlan(List<Recipe> recipes, String scheduledDay) {
        List<MealPlan> mealPlans = new ArrayList<>();
        for (Recipe r : recipes) {
            mealPlans.add(new MealPlan(r.getName(), scheduledDay, r.getImageURL()));
        }
        mRepository.addMealPlans(mealPlans);
    }

    public void addRecipe(Recipe recipe) {
        mRepository.addOrUpdateRecipe(recipe);
    }

    public void addRecipe(Recipe recipe, Callback callback) {
        mRepository.addRecipe(recipe, callback);
    }

    public boolean recipeNameExists(String name) {
        List<Recipe> recipes = this.mRecipesLiveData.getValue();
        if (recipes == null) return false;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
