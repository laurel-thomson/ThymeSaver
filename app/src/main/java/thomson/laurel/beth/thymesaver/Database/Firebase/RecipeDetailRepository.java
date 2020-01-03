package thomson.laurel.beth.thymesaver.Database.Firebase;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.RecipeIngredientsLiveData;
import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.RecipeLiveData;
import thomson.laurel.beth.thymesaver.Database.IRecipeDetailRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class RecipeDetailRepository implements IRecipeDetailRepository {
    private static RecipeDetailRepository mSoleInstance;
    private String mRecipeName;

    public static RecipeDetailRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new RecipeDetailRepository();
        }
        return mSoleInstance;
    }

    private RecipeDetailRepository() {
    }

    @Override
    public void addOrUpdateRecipe(Recipe r) {
        DatabaseReferences.getRecipeReference().child(r.getName()).setValue(r);
    }

    @Override
    public void updateRecipeSteps(String recipeName, List<Step> steps) {
        DatabaseReferences.getRecipeReference().child(recipeName).child("steps").setValue(steps);
    }

    @Override
    public void addSubRecipe(Recipe parent, String childName) {
        DatabaseReferences.getRecipeReference().child(childName).child("subRecipe").setValue(true);
        List<String> subRecipes = parent.getSubRecipes();
        subRecipes.add(childName);
        DatabaseReferences.getRecipeReference().child(parent.getName()).child("subRecipes").setValue(subRecipes);
    }

    @Override
    public void removeSubRecipe(Recipe parent, String childName) {
        List<String> subRecipes = parent.getSubRecipes();
        subRecipes.remove(childName);
        DatabaseReferences.getRecipeReference().child(parent.getName()).child("subRecipes").setValue(subRecipes);
        checkIfIsStillSubRecipe(childName);
    }

    private void checkIfIsStillSubRecipe(String subRecipe) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipe.setName(snap.getKey());
                    if (recipe.getSubRecipes().contains(subRecipe)) {
                        return;
                    }
                }
                DatabaseReferences.getRecipeReference().child(subRecipe).child("subRecipe").setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addUpdateRecipeIngredient(String recipeName, String ingredientName, RecipeQuantity quantity) {
        DatabaseReferences.getRecipeReference()
                .child(recipeName)
                .child("recipeIngredients")
                .child(ingredientName)
                .setValue(quantity);
    }

    @Override
    public void deleteRecipeIngredient(String recipeName, String ingredientName) {
        DatabaseReferences.getRecipeReference()
                .child(recipeName)
                .child("recipeIngredients")
                .child(ingredientName)
                .removeValue();
    }

    @Override
    public void clearAllChecks(String recipeName) {
        DatabaseReferences.getRecipeReference().child(recipeName).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Recipe recipe = dataSnapshot.getValue(Recipe.class);
                        recipe.setName(dataSnapshot.getKey());
                        for (String ingName : recipe.getRecipeIngredients().keySet()) {
                            RecipeQuantity quantity = recipe.getRecipeIngredients().get(ingName);
                            quantity.setChecked(false);
                        }
                        for (Step step : recipe.getSteps()) {
                            step.setChecked(false);
                        }

                        DatabaseReferences.getRecipeReference().child(recipeName).setValue(recipe);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(String recipeName) {
        mRecipeName = recipeName;
        return Transformations.map(
                new RecipeIngredientsLiveData(DatabaseReferences.getPantryReference(), recipeName),
                new RecipeIngredientsDeserializer()
        );
    }

    @Override
    public LiveData<Recipe> getRecipe(String recipeName) {
        return Transformations.map(
                new RecipeLiveData(DatabaseReferences.getRecipeReference().child(recipeName)),
                new RecipeDeserializer());
    }

    @Override
    public void getRecipe(String recipeName, ValueCallback<Recipe> callback) {
        DatabaseReferences.getRecipeReference().child(recipeName).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Recipe recipe = dataSnapshot.getValue(Recipe.class);
                        recipe.setName(dataSnapshot.getKey());
                        callback.onSuccess(recipe);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.toString());
                    }
                }
        );
    }

    private class RecipeIngredientsDeserializer implements Function<DataSnapshot, HashMap<Ingredient, RecipeQuantity>> {
        @Override
        public HashMap<Ingredient, RecipeQuantity> apply(DataSnapshot dataSnapshot) {
            return RecipeIngredientsLiveData.getRecipeIngredients(dataSnapshot, mRecipeName);
        }
    }

    private class RecipeDeserializer implements Function<DataSnapshot, Recipe> {
        @Override
        public Recipe apply(DataSnapshot input) {
            Recipe r = input.getValue(Recipe.class);
            if (r == null) {
                return null;
            }
            r.setName(input.getKey());
            return r;
        }
    }
}
