package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.BulkIngredientState;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.ShoppingListMod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListLiveData extends LiveData<DataSnapshot> {
    private HashMap<Ingredient, Integer> mShoppingList = new HashMap<>();
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

    class MyEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null){
                setValue(dataSnapshot);

                mShoppingList = getShoppingList(dataSnapshot);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public static HashMap<Ingredient, Integer> getShoppingList(DataSnapshot dataSnapshot) {
        HashMap<Ingredient, Integer> shoppingList = new HashMap<>();

        //get all the needed ingredients & quantities from the meal plans
        HashMap<String, Integer> neededIngredients = new HashMap<>();

        for (DataSnapshot snap : dataSnapshot.child("mealplan").getChildren()) {
            MealPlan mealPlan = snap.getValue(MealPlan.class);

            if (mealPlan.isCooked()) continue;

            String recipeName = mealPlan.getRecipeName();

            for (DataSnapshot recipeSnap : dataSnapshot.child("recipes").getChildren()) {
                if (recipeSnap.getKey().equals(recipeName)) {
                    Recipe recipe = recipeSnap.getValue(Recipe.class);
                    for (String ingName : recipe.getRecipeIngredients().keySet()) {
                        if (!neededIngredients.containsKey(ingName)) {
                            neededIngredients.put(
                                    ingName,
                                    recipe.getRecipeIngredients().get(ingName).getRecipeQuantity());
                        }
                        else {
                            neededIngredients.put(
                                    ingName,
                                    neededIngredients.get(ingName)
                                            + recipe.getRecipeIngredients().get(ingName).getRecipeQuantity()
                            );
                        }
                    }
                }
            }
        }

        //get the shopping list mods
        List<ShoppingListMod> mods = new ArrayList<>();
        for (DataSnapshot snap : dataSnapshot.child("shoppinglistmods").getChildren()) {
            ShoppingListMod mod = snap.getValue(ShoppingListMod.class);
            mod.setName(snap.getKey());
            mods.add(mod);
        }

        //find the matching ingredients in the pantry and subtract away from the
        //needed ingredients list if there is quantity in the pantry
        for (DataSnapshot snap : dataSnapshot.child("ingredients").getChildren()) {
            Ingredient i = snap.getValue(Ingredient.class);
            i.setName(snap.getKey());

            if (neededIngredients.containsKey(snap.getKey())) {
                if (i.isBulk()) {
                    if (i.getQuantity() != BulkIngredientState
                            .convertEnumToInt(BulkIngredientState.IN_STOCK)) {
                        shoppingList.put(i, 1);
                    }
                }
                else {
                    int neededQuantity = neededIngredients.get(i.getName());
                    int pantryQuantity = i.getQuantity();
                    if (neededQuantity > pantryQuantity) {
                        shoppingList.put(i, neededQuantity - pantryQuantity);
                    }
                }
            }

            //look for a matching modification
            for (ShoppingListMod mod : mods) {
                if (mod.getName().equals(i.getName())) {
                    switch (mod.getType()) {
                        case CHANGE:
                            if (shoppingList.containsKey(i)) {
                                int updatedQuantity = shoppingList.get(i) + mod.getQuantity();
                                if (updatedQuantity == 0) {
                                    shoppingList.remove(i);
                                }
                                else {
                                    shoppingList.put(i, updatedQuantity);
                                }
                            }
                            else {
                                shoppingList.put(i, mod.getQuantity());
                            }
                            break;
                        case ADD:
                            shoppingList.put(i, 0);
                            break;
                        case DELETE:
                            shoppingList.remove(i);
                            break;
                    }
                    break;
                }
            }
        }
        return shoppingList;
    }
}
