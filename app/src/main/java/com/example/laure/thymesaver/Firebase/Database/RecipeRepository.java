package com.example.laure.thymesaver.Firebase.Database;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRecipeReference;

    private List<Recipe> mRecipes = new ArrayList<>();

    private final LiveData<List<Recipe>> mRecipeLiveData;

    public RecipeRepository() {
        mDatabase = FirebaseDatabase.getInstance();
        mRecipeReference = mDatabase.getReference("recipes");
        mRecipeLiveData = Transformations.map(
                new FirebaseQueryLiveData(mRecipeReference)
                , new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, List<Recipe>> {
        @Override
        public List<Recipe> apply(DataSnapshot dataSnapshot) {
            mRecipes.clear();

            for(DataSnapshot snap : dataSnapshot.getChildren()){
                Recipe r = snap.getValue(Recipe.class);
                mRecipes.add(r);
            }
            return mRecipes;
        }
    }

    @NonNull
    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeLiveData;
    }
}
