package com.example.laure.thymesaver.Database.Firebase.LiveData;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RecipeIngredientsLiveData extends LiveData<DataSnapshot> {
    private HashMap<Ingredient, RecipeQuantity> mRecipeIngredients = new HashMap<>();
    private final Query mQuery;
    private final Recipe mRecipe;
    private final RecipeIngredientsLiveData.MyEventListener mListener = new RecipeIngredientsLiveData.MyEventListener();

    public RecipeIngredientsLiveData(Query q, Recipe recipe) {
        mQuery = q;
        mRecipe = recipe;
    }

    @Override
    protected void onActive() {
        mQuery.addValueEventListener(mListener);
    }

    @Override
    protected void onInactive() {
        mQuery.removeEventListener(mListener);
    }

    private class MyEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null){
                    setValue(dataSnapshot);
                    mRecipeIngredients = getRecipeIngredients(dataSnapshot, mRecipe);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public static HashMap<Ingredient, RecipeQuantity> getRecipeIngredients(DataSnapshot dataSnapshot, Recipe recipe) {
        if (recipe == null) return null;

        HashMap<Ingredient, RecipeQuantity> recipeIngredients = new HashMap<>();
        HashMap<String, RecipeQuantity> neededIngredients = new HashMap<>();

        for (DataSnapshot snap : dataSnapshot.child("recipes")
                .child(recipe.getName())
                .child("recipeIngredients")
                .getChildren()) {
            RecipeQuantity quantity = snap.getValue(RecipeQuantity.class);
            String ingName = snap.getKey();
            neededIngredients.put(ingName, quantity);
        }

        for (DataSnapshot snap : dataSnapshot.child("ingredients").getChildren()) {
            if (neededIngredients.containsKey(snap.getKey())) {
                Ingredient i = snap.getValue(Ingredient.class);
                i.setName(snap.getKey());
                recipeIngredients.put(i, neededIngredients.get(i.getName()));
            }
        }
        return recipeIngredients;
    }
}
