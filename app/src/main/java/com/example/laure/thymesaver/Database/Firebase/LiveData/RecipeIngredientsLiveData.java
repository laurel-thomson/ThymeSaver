package com.example.laure.thymesaver.Database.Firebase.LiveData;

import android.arch.lifecycle.LiveData;
import android.provider.ContactsContract;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

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

        //add the regular ingredients in
        for (String ingName : recipe.getRecipeIngredients().keySet()) {
            DataSnapshot snap = dataSnapshot.child("ingredients").child(ingName);
            Ingredient ing = snap.getValue(Ingredient.class);
            ing.setName(snap.getKey());
            recipeIngredients.put(ing, recipe.getRecipeIngredients().get(ingName));
        }

        //add the sub recipe ingredients in
        for (String subRecipeName : recipe.getSubRecipes()) {
            DataSnapshot snap = dataSnapshot.child("recipes").child(subRecipeName);
            Recipe subRecipe = snap.getValue(Recipe.class);
            subRecipe.setName(snap.getKey());
            for (String ingName : subRecipe.getRecipeIngredients().keySet()) {
                DataSnapshot snap2 = dataSnapshot.child("ingredients").child(ingName);
                Ingredient ing = snap2.getValue(Ingredient.class);
                ing.setName(snap2.getKey());
                RecipeQuantity quantity = subRecipe.getRecipeIngredients().get(ingName);
                quantity.setSubRecipe(subRecipeName);
                recipeIngredients.put(ing, quantity);
            }
        }
        return recipeIngredients;
    }
}
