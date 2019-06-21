package com.example.laure.thymesaver.Firebase.Database.Repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.LiveData.ListLiveData;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PantryRepository implements IPantryRepository {
    private static PantryRepository mSoleInstance;
    private List<Ingredient> mIngredients = new ArrayList<>();

    public static PantryRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new PantryRepository();
        }
        return mSoleInstance;
    }

    private PantryRepository() {
    }

    @Override
    public void addIngredient(Ingredient i) {
        DatabaseReferences.getIngredientReference().child(i.getName()).setValue(i);
    }

    @Override
    public void deleteIngredient(Ingredient i) {
        DatabaseReferences.getIngredientReference().child(i.getName()).removeValue();
        removeIngredientFromRecipes(i);
    }

    private void removeIngredientFromRecipes(Ingredient i) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipe.setName(snap.getKey());
                    if (recipe.getRecipeIngredients().containsKey(i.getName())) {
                        recipe.getRecipeIngredients().remove(i.getName());
                        CookbookRepository.getInstance().addOrUpdateRecipe(recipe);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateIngredient(Ingredient i) {
        DatabaseReferences.getIngredientReference().child(i.getName()).setValue(i);
    }

    @Override
    public void getIngredient(String ingredientName, ValueCallback<Ingredient> callback) {
        DatabaseReferences.getIngredientReference().child(ingredientName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            callback.onError("The ingredient does not exist");
                            return;
                        }
                        Ingredient ing = dataSnapshot.getValue(Ingredient.class);
                        ing.setName(dataSnapshot.getKey());
                        callback.onSuccess(ing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.toString());
                    }
                });
    }

    @Override
    public LiveData<List<Ingredient>> getAllIngredients() {
        return Transformations.map(
                new ListLiveData<Ingredient>(DatabaseReferences.getIngredientReference(), Ingredient.class),
                new IngredientDeserializer());
    }

    private class IngredientDeserializer implements Function<DataSnapshot, List<Ingredient>> {
        @Override
        public List<Ingredient> apply(DataSnapshot dataSnapshot) {
            mIngredients.clear();

            for(DataSnapshot snap : dataSnapshot.getChildren()){
                Ingredient i = snap.getValue(Ingredient.class);
                i.setName(snap.getKey());
                mIngredients.add(i);
            }
            return mIngredients;
        }
    }
}
