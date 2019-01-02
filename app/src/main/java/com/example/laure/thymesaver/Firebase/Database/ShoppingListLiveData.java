package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListLiveData extends LiveData<DataSnapshot> {
    private HashMap<String, Integer> mShoppingList = new HashMap<>();
    private final Query mQuery;
    private final ShoppingListLiveData.MyEventListener mListener = new ShoppingListLiveData.MyEventListener();

    public ShoppingListLiveData(Query q) {
        mQuery = q;
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

                HashMap<String, Integer> neededIngredients = new HashMap<>();

                for (DataSnapshot snap : dataSnapshot.child("mealplan").getChildren()) {
                    MealPlan mealPlan = snap.getValue(MealPlan.class);
                    String recipeName = mealPlan.getRecipeName();

                    for (DataSnapshot recipeSnap : dataSnapshot.child("recipes").getChildren()) {
                        if (recipeSnap.getKey().equals(recipeName)) {
                            Recipe recipe = recipeSnap.getValue(Recipe.class);
                            for (String ingName : recipe.getRecipeIngredients().keySet()) {
                                if (!neededIngredients.containsKey(ingName)) {
                                    neededIngredients.put(ingName, recipe.getRecipeIngredients().get(ingName));
                                }
                                else {
                                    neededIngredients.put(
                                            ingName,
                                            neededIngredients.get(ingName)
                                                    + recipe.getRecipeIngredients().get(ingName)
                                    );
                                }
                            }
                        }
                    }
                }

                for (DataSnapshot snap : dataSnapshot.child("ingredients").getChildren()) {
                    if (neededIngredients.containsKey(snap.getKey())) {
                        Ingredient i = snap.getValue(Ingredient.class);
                        i.setName(snap.getKey());
                        int neededQuantity = neededIngredients.get(i.getName());
                        int pantryQuantity = i.getQuantity();
                        if (neededQuantity > pantryQuantity) {
                            mShoppingList.put(i.getName(), neededQuantity - pantryQuantity);
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
