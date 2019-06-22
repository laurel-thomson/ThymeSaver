package com.example.laure.thymesaver.Database.Firebase;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Database.Firebase.LiveData.ListLiveData;
import com.example.laure.thymesaver.Database.IMealPlanRepository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;
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
    public void removeMealPlanIngredientsFromPantry(MealPlan mealPlan, ValueCallback<HashMap> callback) {
        DatabaseReferences.getRecipeReference().equalTo(mealPlan.getRecipeName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //There should only be one matching recipe
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipe.setName(snap.getKey());
                    removeRecipeIngredientsFromPantry(recipe, callback);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeRecipeIngredientsFromPantry(Recipe recipe, ValueCallback<HashMap> callback) {
        DatabaseReferences.getIngredientReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap newIngredientData = new HashMap();
                        HashMap oldIngredientData = new HashMap();

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            if (recipe.getRecipeIngredients().containsKey(snap.getKey())) {
                                Ingredient ing = snap.getValue(Ingredient.class);
                                ing.setName(snap.getKey());

                                if (ing.isBulk()) continue; //we only update non bulk quantities

                                oldIngredientData.put(ing.getName(), ing);

                                //make a copy of the old ingredient (need to make a copy so that we
                                //can send back the original in the callback (used for undoing)

                                Ingredient newIng = new Ingredient(ing.getName(), ing.getCategory(), ing.isBulk());
                                newIng.setQuantity(Math.max(0, ing.getQuantity() -
                                        (int) Math.ceil(recipe.getRecipeIngredients().get(ing.getName()).getRecipeQuantity())));

                                newIngredientData.put(newIng.getName(), newIng);
                            }
                        }
                        DatabaseReferences.getIngredientReference().updateChildren(newIngredientData);
                        callback.onSuccess(oldIngredientData);
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
