package com.example.laure.thymesaver.Firebase.Database.Repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.LiveData.ListLiveData;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CookbookRepository implements ICookbookRepository {
    private List<Recipe> mRecipes = new ArrayList<>();
    private static CookbookRepository mSoleInstance;

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
    public void deleteRecipe(Recipe r) {
        DatabaseReferences.getRecipeReference().child(r.getName()).removeValue();

        deleteAssociatedMealPlans(r.getName());
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
                            if (mealPlan.getRecipeName().equals(recipeName)) {
                                DatabaseReferences.getMealPlanReference().child(mealPlan.getRecipeName())
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
}
