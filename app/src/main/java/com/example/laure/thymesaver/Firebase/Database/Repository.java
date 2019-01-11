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
    private HashMap<String, Integer> mShoppingList = new HashMap<>();
    private final LiveData<List<Recipe>> mRecipeLiveData;
    private final LiveData<List<Ingredient>> mIngredientLiveData;
    private final LiveData<List<MealPlan>> mMealPlanLiveData;
    private final LiveData<HashMap<String,Integer>> mShoppingLiveData;

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
        mShoppingLiveData = Transformations.map(
                new ShoppingListLiveData(mDatabase.getReference()),
                new ShoppingListDeserializer());


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

    public void addQuantityToIngredient(String ingName, final int quantity) {
        mIngredientReference.child(ingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ingredient i = dataSnapshot.getValue(Ingredient.class);
                i.setName(dataSnapshot.getKey());
                i.setQuantity(i.getQuantity() + quantity);
                updateIngredient(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void removeQuantityFromIngredient(String ingName, final int quantity) {
        mIngredientReference.child(ingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ingredient i = dataSnapshot.getValue(Ingredient.class);
                i.setName(dataSnapshot.getKey());
                i.setQuantity(Math.max(0, i.getQuantity() - quantity));
                updateIngredient(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMealPlan(MealPlan mealPlan) {
        mMealPlanReference.push().setValue(mealPlan);
    }

    public void addMealPlans(List<MealPlan> mealPlans) {
        for (MealPlan m : mealPlans) {
            mMealPlanReference.push().setValue(m);
        }
    }

    public void updateMealPlan(MealPlan mealPlan) {
        mMealPlanReference.child(mealPlan.getFirebaseKey()).setValue(mealPlan);
    }

    public void removeMealPlan(MealPlan mealPlan) {
        mMealPlanReference.child(mealPlan.getFirebaseKey()).removeValue();
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
    public LiveData<HashMap<String, Integer>> getShoppingList() {
        return mShoppingLiveData;
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

    private class ShoppingListDeserializer implements  Function<DataSnapshot, HashMap<String, Integer>> {

        @Override
        public HashMap<String, Integer> apply(DataSnapshot dataSnapshot) {
            mShoppingList.clear();

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
            return mShoppingList;
        }
    }
}
