package com.example.laure.thymesaver.Firebase.Database;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRecipeReference;
    private DatabaseReference mIngredientReference;
    private DatabaseReference mMealPlanReference;
    private static Repository mSoleInstance;
    private List<Recipe> mRecipes = new ArrayList<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<MealPlan> mMealPlans = new ArrayList<>();
    private final LiveData<List<Recipe>> mRecipeLiveData;
    private final LiveData<List<Ingredient>> mIngredientLiveData;
    private final LiveData<List<MealPlan>> mMealPlanLiveData;

    public static Repository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new Repository();
        }
        return mSoleInstance;
    }

    private Repository() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mRecipeReference = mDatabase.getReference("recipes");
        mIngredientReference = mDatabase.getReference("ingredients");
        mMealPlanReference = mDatabase.getReference("mealplan");
        mRecipeLiveData = Transformations.map(
                new FirebaseQueryLiveData<Recipe>(mRecipeReference, Recipe.class),
                new RecipeListDeserializer());
        mIngredientLiveData = Transformations.map(
                new FirebaseQueryLiveData<Ingredient>(mIngredientReference, Ingredient.class),
                new IngredientDeserializer());
        mMealPlanLiveData = Transformations.map(
                new FirebaseQueryLiveData<MealPlan>(mMealPlanReference, MealPlan.class),
                new MealPlanDeserializer());


        //force recipes & ingredients to cache
        mRecipeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Recipe r = snap.getValue(Recipe.class);
                    r.setName(snap.getKey());
                    mRecipes.add(r);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mIngredientReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Ingredient i = snap.getValue(Ingredient.class);
                    i.setName(snap.getKey());
                    mIngredients.add(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addOrUpdateRecipe(Recipe r) {
        mRecipeReference.child(r.getName()).setValue(r);
    }

    public void addIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).setValue(i);
    }

    public void updateIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).setValue(i);
    }

    public void addMealPlan(MealPlan mealPlan) {
        mMealPlanReference.push().setValue(mealPlan);
    }

    public void updateMealPlan(MealPlan mealPlan) {
        mMealPlanReference.child(mealPlan.getFirebaseKey()).setValue(mealPlan);
    }

    public void removeMealPlanIngredientsFromPantry(MealPlan mealPlan) {
        HashMap<String, Integer> recipeIngredients = null;
        for (Recipe recipe : mRecipes) {
            if (recipe.getName().equals(mealPlan.getRecipeName())) {
                recipeIngredients = recipe.getRecipeIngredients();
                break;
            }
        }
        HashMap ingredientData = new HashMap();
        for (String ingredientName : recipeIngredients.keySet()) {
            Ingredient matchingIngredient = null;
            for (Ingredient i : mIngredients) {
                if (i.getName().equals(ingredientName)) {
                    matchingIngredient = i;
                    break;
                }
            }
            matchingIngredient.setQuantity(
                    Math.max(0, matchingIngredient.getQuantity() - recipeIngredients.get(ingredientName)));
            ingredientData.put(ingredientName,matchingIngredient);
        }
        mIngredientReference.updateChildren(ingredientData);
    }

    public void addMealPlanIngredientsToPantry(MealPlan mealPlan) {
        HashMap<String, Integer> recipeIngredients = null;
        for (Recipe recipe : mRecipes) {
            if (recipe.getName().equals(mealPlan.getRecipeName())) {
                recipeIngredients = recipe.getRecipeIngredients();
                break;
            }
        }
        HashMap ingredientData = new HashMap();
        for (String ingredientName : recipeIngredients.keySet()) {
            Ingredient matchingIngredient = null;
            for (Ingredient i : mIngredients) {
                if (i.getName().equals(ingredientName)) {
                    matchingIngredient = i;
                    break;
                }
            }
            matchingIngredient.setQuantity(
                    matchingIngredient.getQuantity() + recipeIngredients.get(ingredientName));
            ingredientData.put(ingredientName,matchingIngredient);
        }
        mIngredientReference.updateChildren(ingredientData);
    }

    @NonNull
    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeLiveData;
    }

    @NonNull
    public LiveData<List<Ingredient>> getAllIngredients() {
        return mIngredientLiveData;
    }

    @NonNull
    public LiveData<List<MealPlan>> getMealPlans() {
        return mMealPlanLiveData;
    }

    public Recipe getRecipe(String recipeName) {
        for (Recipe r : mRecipes) {
            if (r.getName().equals(recipeName))
                return r;
        }
        throw new Resources.NotFoundException();
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
