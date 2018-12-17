package com.example.laure.thymesaver.Firebase.Database;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MeasuredIngredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRecipeReference;
    private DatabaseReference mIngredientReference;
    private static Repository mSoleInstance;
    private List<Recipe> mRecipes = new ArrayList<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private final LiveData<List<Recipe>> mRecipeLiveData;
    private final LiveData<List<Ingredient>> mIngredientLiveData;

    public static Repository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new Repository();
        }
        return mSoleInstance;
    }

    private Repository() {
        mDatabase = FirebaseDatabase.getInstance();
        mRecipeReference = mDatabase.getReference("recipes");
        mIngredientReference = mDatabase.getReference("ingredients");
        mRecipeLiveData = Transformations.map(
                new FirebaseQueryLiveData<Recipe>(mRecipeReference, Recipe.class),
                new RecipeListDeserializer());
        mIngredientLiveData = Transformations.map(
                new FirebaseQueryLiveData<Ingredient>(mIngredientReference, Ingredient.class),
                new IngredientDeserializer());
    }

    public void addRecipe(Recipe r) {
        mRecipeReference.child(r.getName()).setValue(r);
    }

    public void addIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).setValue(i);
    }

    @NonNull
    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeLiveData;
    }

    @NonNull
    public LiveData<List<Ingredient>> getAllIngredients() {
        return mIngredientLiveData;
    }

    public Recipe getRecipe(String recipeName) {
        for (Recipe r : mRecipes) {
            if (r.getName().equals(recipeName))
                return r;
        }
        throw new Resources.NotFoundException();
    }

    public List<Ingredient> getRecipeIngredients(Recipe recipe) {
        List<Ingredient> measuredIngredients = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : recipe.getRecipeIngredients().entrySet()) {
            String ingredientName = entry.getKey();
            int quantity = entry.getValue();

            for (Ingredient i : mIngredients) {
                if (i.getName().equals(ingredientName));
                measuredIngredients.add(new MeasuredIngredient(i, quantity));
            }
        }
        return measuredIngredients;
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
}