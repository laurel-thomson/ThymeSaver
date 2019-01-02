package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

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

                HashMap<String, HashMap<String, Integer>> mealPlans = new HashMap<>();
                for (DataSnapshot snap : dataSnapshot.child("mealplan").getChildren()) {
                    MealPlan mealPlan = snap.getValue(MealPlan.class);
                    mealPlans.put(mealPlan.getRecipeName(), null);
                }

                for (DataSnapshot snap : dataSnapshot.child("recipes").getChildren()) {
                    if (mealPlans.containsKey(snap.getKey())) {
                        Recipe recipe = snap.getValue(Recipe.class);
                        mealPlans.put(snap.getKey(), recipe.getRecipeIngredients());
                    }
                }

                HashMap<String, Integer> neededIngredients = new HashMap<>();

                for (String recipeName : mealPlans.keySet()) {
                    for (String ingName : mealPlans.get(recipeName).keySet()) {
                        if (!neededIngredients.containsKey(ingName)) {
                            neededIngredients.put(ingName, mealPlans.get(recipeName).get(ingName));
                        }
                        else {
                            neededIngredients.put(
                                    ingName,
                                    neededIngredients.get(ingName) + mealPlans.get(recipeName).get(ingName)
                            );
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
