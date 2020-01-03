package thomson.laurel.beth.thymesaver.Database.Firebase;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.ListLiveData;
import thomson.laurel.beth.thymesaver.Database.IMealPlanRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealPlanRepository implements IMealPlanRepository {
    private static MealPlanRepository mSoleInstance;
    private List<MealPlan> mMealPlans = new ArrayList<>();

    public static MealPlanRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new MealPlanRepository();
        }
        return mSoleInstance;
    }

    private MealPlanRepository() {
    }

    @Override
    public void addMealPlan(MealPlan mealPlan) {
        DatabaseReferences.getMealPlanReference().push().setValue(mealPlan);
    }


    @Override
    public void updateMealPlan(MealPlan mealPlan) {
        DatabaseReferences.getMealPlanReference().child(mealPlan.getFirebaseKey()).setValue(mealPlan);
    }

    @Override
    public void deleteMealPlan(MealPlan mealPlan) {
        DatabaseReferences.getMealPlanReference().child(mealPlan.getFirebaseKey()).removeValue();
    }

    @Override
    public void cookMealPlan(MealPlan mealPlan, ValueCallback<HashMap> callback) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                List<Recipe> recipes = new ArrayList<>();
                Recipe recipe = snap.child(mealPlan.getRecipeName()).getValue(Recipe.class);
                recipe.setName(snap.child(mealPlan.getRecipeName()).getKey());
                recipes.add(recipe);

                for (String recipeName : recipe.getSubRecipes()) {
                    Recipe r = snap.child(recipeName).getValue(Recipe.class);
                    r.setName(snap.child(recipeName).getKey());
                    recipes.add(r);
                }
                cookRecipes(recipes, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cookRecipes(List<Recipe> recipes, ValueCallback<HashMap> callback) {
        clearChecks(recipes);
        removeRecipeIngredientsFromPantry(recipes, callback);
    }

    private void clearChecks(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            for (String ingName : recipe.getRecipeIngredients().keySet()) {
                RecipeQuantity quantity = recipe.getRecipeIngredients().get(ingName);
                quantity.setChecked(false);
            }
            for (Step step : recipe.getSteps()) {
                step.setChecked(false);
            }
            DatabaseReferences.getRecipeReference().child(recipe.getName()).setValue(recipe);
        }
    }

    private void removeRecipeIngredientsFromPantry(List<Recipe> recipes, ValueCallback<HashMap> callback) {
        DatabaseReferences.getIngredientReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap newIngredientData = new HashMap();
                        HashMap oldIngredientData = new HashMap();

                        for (Recipe recipe : recipes) {
                            for (String ingName : recipe.getRecipeIngredients().keySet()) {
                                DataSnapshot snap = dataSnapshot.child(ingName);
                                Ingredient ing = snap.getValue(Ingredient.class);
                                ing.setName(snap.getKey());

                                if (ing.isBulk()) continue; //we only update non bulk quantities

                                if (newIngredientData.containsKey(ingName)) {
                                    Ingredient newIng = (Ingredient) newIngredientData.get(ingName);
                                    newIng.setQuantity(Math.max(0, ing.getQuantity() -
                                            (int) Math.ceil(recipe.getRecipeIngredients().get(ing.getName()).getRecipeQuantity())));
                                }
                                else {
                                    //make a copy of the old ingredient (need to make a copy so that we
                                    //can send back the original in the callback (used for undoing)
                                    oldIngredientData.put(ing.getName(), ing);

                                    Ingredient newIng = new Ingredient(ing.getName(), ing.getCategory(), ing.isBulk());
                                    newIng.setQuantity(Math.max(0, ing.getQuantity() -
                                            (int) Math.ceil(recipe.getRecipeIngredients().get(ing.getName()).getRecipeQuantity())));

                                    newIngredientData.put(newIng.getName(), newIng);
                                }
                            }

                            DatabaseReferences.getIngredientReference().updateChildren(newIngredientData);
                            callback.onSuccess(oldIngredientData);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void addMealPlanIngredientsToPantry(HashMap ingredientQuantites) {
        DatabaseReferences.getIngredientReference().updateChildren(ingredientQuantites);
    }

    @Override
    public LiveData<List<MealPlan>> getMealPlans() {
        return Transformations.map(
                new ListLiveData<MealPlan>(DatabaseReferences.getMealPlanReference(), MealPlan.class),
                new MealPlanDeserializer());
    }

    private class MealPlanDeserializer implements Function<DataSnapshot, List<MealPlan>> {
        @Override
        public List<MealPlan> apply(DataSnapshot dataSnapshot) {
            mMealPlans.clear();

            for(DataSnapshot snap : dataSnapshot.getChildren()){
                MealPlan m = snap.getValue(MealPlan.class);
                m.setFirebaseKey(snap.getKey());
                mMealPlans.add(m);
            }
            return mMealPlans;
        }
    }
}
