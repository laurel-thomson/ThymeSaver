package thomson.laurel.beth.thymesaver.ViewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Database.IRecipeDetailRepository;
import thomson.laurel.beth.thymesaver.Database.Firebase.RecipeDetailRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import java.util.HashMap;
import java.util.List;

public class RecipeDetailViewModel extends AndroidViewModel {
    private IRecipeDetailRepository mRepository;
    private Recipe mCurrentRecipe;
    private String mCurrentRecipeName;

    public RecipeDetailViewModel(Application application) {
        super(application);
        mRepository = RecipeDetailRepository.getInstance();
    }

    public void setCurrentRecipe(String currentRecipeName) {
        mCurrentRecipeName = currentRecipeName;
    }

    public void getCurrentRecipe(ValueCallback<Recipe> callback) {
        mRepository.getRecipe(mCurrentRecipeName, new ValueCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe value) {
                mCurrentRecipe = value;
                callback.onSuccess(value);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public LiveData<Recipe> getLiveRecipe() {
        return mRepository.getRecipe(mCurrentRecipeName);
    }

    public Recipe getCurrentRecipe() {
        return mCurrentRecipe;
    }

    public void updateRecipe() {
        mRepository.addOrUpdateRecipe(mCurrentRecipe);
    }

    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients() {
        return mRepository.getRecipeIngredients(mCurrentRecipeName);
    }

    public void addUpdateRecipeIngredient(String recipeName, String ingredientName, RecipeQuantity quantity) {
        mRepository.addUpdateRecipeIngredient(recipeName, ingredientName, quantity);
    }

    public void addSubRecipes(String[] subRecipes) {
        Recipe parent = mCurrentRecipe;
        for (String s : subRecipes) {
            mRepository.addSubRecipe(parent, s);
        }
    }

    public void addSubRecipe(String subRecipe) {
        mRepository.addSubRecipe(mCurrentRecipe, subRecipe);
    }

    public void deleteRecipeIngredient(String recipeName, String ingredientName) {
        mRepository.deleteRecipeIngredient(recipeName, ingredientName);
    }

    public void removeSubRecipe(String subRecipeName) {
        mRepository.removeSubRecipe(mCurrentRecipe, subRecipeName);
    }

    public void addStep(Step step) {
        mCurrentRecipe.getSteps().add(step);
        mRepository.updateRecipeSteps(mCurrentRecipe.getName(), mCurrentRecipe.getSteps());
    }

    public void updateStep(int position, Step step) {
        mCurrentRecipe.getSteps().set(position, step);
        mRepository.updateRecipeSteps(mCurrentRecipe.getName(), mCurrentRecipe.getSteps());
    }

    public void updateSteps(List<Step> steps) {
        mCurrentRecipe.setSteps(steps);
        mRepository.updateRecipeSteps(mCurrentRecipe.getName(), mCurrentRecipe.getSteps());
    }

    public Step deleteStep(int position) {
        Step step = mCurrentRecipe.getSteps().get(position);
        mCurrentRecipe.getSteps().remove(step);
        mRepository.updateRecipeSteps(mCurrentRecipe.getName(), mCurrentRecipe.getSteps());
        return step;
    }

    public void clearAllChecks() {
        mRepository.clearAllChecks(mCurrentRecipe.getName());
        for (String subRecipe : mCurrentRecipe.getSubRecipes()) {
            mRepository.clearAllChecks(subRecipe);
        }
    }

}
