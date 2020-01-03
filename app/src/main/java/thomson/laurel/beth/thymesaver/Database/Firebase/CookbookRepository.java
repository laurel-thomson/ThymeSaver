package thomson.laurel.beth.thymesaver.Database.Firebase;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.ListLiveData;
import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.SubRecipeListLiveData;
import thomson.laurel.beth.thymesaver.Database.ICookbookRepository;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CookbookRepository implements ICookbookRepository {
    private List<Recipe> mRecipes = new ArrayList<>();
    private static CookbookRepository mSoleInstance;
    private String mParentRecipe;

    public static CookbookRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new CookbookRepository();
        }
        return mSoleInstance;
    }

    private CookbookRepository() {
    }

    @Override
    public void addOrUpdateRecipe(Recipe r) {
        DatabaseReferences.getRecipeReference().child(r.getName()).setValue(r);
    }

    @Override
    public void addRecipe(Recipe r, Callback callback) {
        DatabaseReferences.getRecipeReference().child(r.getName()).setValue(r,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            callback.onSuccess();
                    }
                });
    }


    @Override
    public void deleteRecipe(Recipe r) {
        DatabaseReferences.getRecipeReference().child(r.getName()).removeValue();
        deleteAssociatedMealPlans(r.getName());
        if (r.isSubRecipe()) {
            removeFromParentRecipes(r.getName());
        }
    }

    private void removeFromParentRecipes(String subRecipeName) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipe.setName(snap.getKey());
                    if (recipe.getSubRecipes().contains(subRecipeName)) {
                        recipe.getSubRecipes().remove(subRecipeName);
                        DatabaseReferences.getRecipeReference()
                                .child(recipe.getName())
                                .child("subRecipes")
                                .setValue(recipe.getSubRecipes());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addMealPlans(List<MealPlan> mealPlans) {
        for (MealPlan m : mealPlans) {
            DatabaseReferences.getMealPlanReference().push().setValue(m);
        }
    }

    private void deleteAssociatedMealPlans(String recipeName) {
        DatabaseReferences.getMealPlanReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            MealPlan mealPlan = snap.getValue(MealPlan.class);
                            mealPlan.setFirebaseKey(snap.getKey());
                            if (mealPlan.getRecipeName().equals(recipeName)) {
                                DatabaseReferences.getMealPlanReference().child(mealPlan.getFirebaseKey())
                                        .removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public LiveData<List<Recipe>> getAllRecipes() {
        return Transformations.map(
                new ListLiveData<Recipe>(DatabaseReferences.getRecipeReference(), Recipe.class),
                new RecipeListDeserializer());
    }

    @Override
    public void getAllRecipes(ValueCallback<List<Recipe>> callback) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRecipes.clear();

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Recipe r = snap.getValue(Recipe.class);
                    r.setName(snap.getKey());
                    mRecipes.add(r);
                }
                callback.onSuccess(mRecipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toString());
            }
        });
    }

    @Override
    public LiveData<List<Recipe>> getAvailableSubRecipes(String parentRecipeName) {
        mParentRecipe = parentRecipeName;
        return Transformations.map(
                new SubRecipeListLiveData(DatabaseReferences.getRecipeReference(), parentRecipeName),
                new SubRecipeListDeserializer());
    }

    private class RecipeListDeserializer implements Function<DataSnapshot, List<Recipe>> {
        @Override
        public List<Recipe> apply(DataSnapshot dataSnapshot) {
            mRecipes.clear();

            for(DataSnapshot snap : dataSnapshot.getChildren()){
                Recipe r = snap.getValue(Recipe.class);
                r.setName(snap.getKey());
                mRecipes.add(r);
            }
            return mRecipes;
        }
    }

    private class SubRecipeListDeserializer implements Function<DataSnapshot, List<Recipe>> {
        @Override
        public List<Recipe> apply(DataSnapshot input) {
            return SubRecipeListLiveData.getAvailableSubRecipes(input, mParentRecipe);
        }
    }
}
