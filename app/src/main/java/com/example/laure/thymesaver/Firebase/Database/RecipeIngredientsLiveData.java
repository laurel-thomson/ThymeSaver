package com.example.laure.thymesaver.Firebase.Database;

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

                if (mRecipe == null) return;

                HashMap<String, RecipeQuantity> neededIngredients = new HashMap<>();

                for (DataSnapshot snap : dataSnapshot.child("recipes")
                        .child(mRecipe.getName())
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
                        mRecipeIngredients.put(i, neededIngredients.get(i.getName()));
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
