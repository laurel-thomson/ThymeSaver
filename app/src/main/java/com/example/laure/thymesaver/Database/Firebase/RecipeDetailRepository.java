package com.example.laure.thymesaver.Database.Firebase;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import com.example.laure.thymesaver.Database.Firebase.LiveData.RecipeIngredientsLiveData;
import com.example.laure.thymesaver.Database.Firebase.LiveData.RecipeLiveData;
import com.example.laure.thymesaver.Database.IRecipeDetailRepository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class RecipeDetailRepository implements IRecipeDetailRepository {
    private static RecipeDetailRepository mSoleInstance;
    private Recipe mRecipe;

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
    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(Recipe r) {
        mRecipe = r;
        return Transformations.map(
                new RecipeIngredientsLiveData(DatabaseReferences.getPantryReference(), r),
                new RecipeIngredientsDeserializer()
        );
    }

    @Override
    public LiveData<Recipe> getRecipe(String recipeName) {
        return Transformations.map(
                new RecipeLiveData(DatabaseReferences.getRecipeReference().child(recipeName)),
                new RecipeDeserializer());
    }

    private class RecipeIngredientsDeserializer implements Function<DataSnapshot, HashMap<Ingredient, RecipeQuantity>> {
        @Override
        public HashMap<Ingredient, RecipeQuantity> apply(DataSnapshot dataSnapshot) {
            return RecipeIngredientsLiveData.getRecipeIngredients(dataSnapshot, mRecipe);
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
