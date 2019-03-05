package com.example.laure.thymesaver.Firebase.Database;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Models.BulkIngredientState;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.Models.ShoppingListMod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.laure.thymesaver.Models.ModType.CHANGE;

public class Repository {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRecipeReference;
    private DatabaseReference mIngredientReference;
    private DatabaseReference mMealPlanReference;
    private static Repository mSoleInstance;
    private List<Recipe> mRecipes = new ArrayList<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<MealPlan> mMealPlans = new ArrayList<>();
    private HashMap<Ingredient, Integer> mShoppingList = new HashMap<>();
    private final LiveData<List<Recipe>> mRecipeListLiveData;
    private final LiveData<List<Ingredient>> mIngredientLiveData;
    private final LiveData<List<MealPlan>> mMealPlanLiveData;
    private final LiveData<HashMap<Ingredient,Integer>> mShoppingLiveData;
    private LiveData<Recipe> mRecipeLiveData;
    private Recipe mRecipe;

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
        mRecipeListLiveData = Transformations.map(
                new ListLiveData<Recipe>(mRecipeReference, Recipe.class),
                new RecipeListDeserializer());
        mIngredientLiveData = Transformations.map(
                new ListLiveData<Ingredient>(mIngredientReference, Ingredient.class),
                new IngredientDeserializer());
        mMealPlanLiveData = Transformations.map(
                new ListLiveData<MealPlan>(mMealPlanReference, MealPlan.class),
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

    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(Recipe r) {
        mRecipe = r;
        return Transformations.map(
                        new RecipeIngredientsLiveData(mDatabase.getReference(), r),
                        new RecipeIngredientsDeserializer()
                );
    }

    public void addOrUpdateRecipe(Recipe r) {
        mRecipeReference.child(r.getName()).setValue(r);
    }

    public void deleteRecipe(Recipe r) {
        mRecipeReference.child(r.getName()).removeValue();

        //delete all meal plans with this reference
        for (MealPlan mp : mMealPlans) {
            mMealPlanReference.child(mp.getFirebaseKey()).removeValue();
        }
    }

    public void addIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).setValue(i);
    }

    public void deleteIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).removeValue();

        //remove this ingredient from all recipes
        for (Recipe r : mRecipes) {
            r.getRecipeIngredients().remove(i.getName());
            addOrUpdateRecipe(r);
        }
    }

    public void updateIngredient(Ingredient i) {
        mIngredientReference.child(i.getName()).setValue(i);
    }

    public Ingredient getIngredient(String ingredientName) {
        for (Ingredient i : mIngredients) {
            if (i.getName().equals(ingredientName)) {
                return i;
            }
        }
        return null;
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

    public void deleteMealPlan(MealPlan mealPlan) {
        mMealPlanReference.child(mealPlan.getFirebaseKey()).removeValue();
    }

    public void removeMealPlanIngredientsFromPantry(MealPlan mealPlan) {
        HashMap<String, RecipeQuantity> recipeIngredients = null;
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
            // we only want to remove quantity from non-bulk ingredients
            if (matchingIngredient.isBulk()) continue;
            matchingIngredient.setQuantity(
                    Math.max(0, matchingIngredient.getQuantity() - recipeIngredients.get(ingredientName).getRecipeQuantity()));
            ingredientData.put(ingredientName,matchingIngredient);
        }
        mIngredientReference.updateChildren(ingredientData);
    }

    public void addMealPlanIngredientsToPantry(MealPlan mealPlan) {
        HashMap<String, RecipeQuantity> recipeIngredients = null;
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
            // we only want to add quantity from non-bulk ingredients
            if (matchingIngredient.isBulk()) continue;
            matchingIngredient.setQuantity(
                    matchingIngredient.getQuantity()
                            + recipeIngredients.get(ingredientName).getRecipeQuantity());
            ingredientData.put(ingredientName,matchingIngredient);
        }
        mIngredientReference.updateChildren(ingredientData);
    }

    public void addOrUpdateModification(final ShoppingListMod mod) {
        mDatabase.getReference("shoppinglistmods").child(mod.getName()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ShoppingListMod oldMod = dataSnapshot.getValue(ShoppingListMod.class);
                            oldMod.setQuantity(oldMod.getQuantity() + mod.getQuantity());
                            mDatabase.getReference("shoppinglistmods").child(mod.getName()).setValue(oldMod);
                        }
                        else {
                            mDatabase.getReference("shoppinglistmods").child(mod.getName()).setValue(mod);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void deleteShoppingModification(final String name) {
        mDatabase.getReference("shoppinglistmods").child(name).removeValue();
    }

    public void deleteAllModifications() {
        mDatabase.getReference("shoppinglistmods").removeValue();
    }

    public void deleteShoppingListItem(final Ingredient ingredient, final int quantity) {
        mDatabase.getReference("shoppinglistmods").child(ingredient.getName()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ShoppingListMod mod;
                            if (ingredient.isBulk()) {
                                mod = new ShoppingListMod(ingredient.getName(), ModType.DELETE, 0);
                            }
                            else {
                                mod = new ShoppingListMod(ingredient.getName(), CHANGE, 0 - quantity);
                            }
                            addOrUpdateModification(mod);
                        }
                        else {
                            //if we are deleting a shopping list item that has a modification, it
                            //must be a change or add mod, and in this case, we want to get rid of it
                            deleteShoppingModification(ingredient.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @NonNull
    public LiveData<List<Recipe>> getAllRecipes() {
        return mRecipeListLiveData;
    }

    @NonNull
    public LiveData<List<Ingredient>> getAllIngredients() {
        return mIngredientLiveData;
    }

    @NonNull
    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {
        return mShoppingLiveData;
    }

    @NonNull
    public LiveData<List<MealPlan>> getMealPlans() {
        return mMealPlanLiveData;
    }

    public LiveData<Recipe> getRecipe(String recipeName) {
        mRecipeLiveData = Transformations.map(
                new RecipeLiveData(mRecipeReference.child(recipeName)),
                new RecipeDeserializer());
        return mRecipeLiveData;
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

    private class RecipeIngredientsDeserializer implements Function<DataSnapshot, HashMap<Ingredient, RecipeQuantity>> {

        @Override
        public HashMap<Ingredient, RecipeQuantity> apply(DataSnapshot dataSnapshot) {

            if (mRecipe == null) return null;

            HashMap<Ingredient, RecipeQuantity> recipeIngredients = new HashMap<>();
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
                    recipeIngredients.put(i, neededIngredients.get(i.getName()));
                }
            }
            return recipeIngredients;
        }
    }

    private class ShoppingListDeserializer implements  Function<DataSnapshot, HashMap<Ingredient, Integer>> {

        @Override
        public HashMap<Ingredient, Integer> apply(DataSnapshot dataSnapshot) {
            mShoppingList = getShoppingList(dataSnapshot);
            return mShoppingList;
        }
    }

    public static HashMap<Ingredient, Integer> getShoppingList(DataSnapshot dataSnapshot) {
        HashMap<Ingredient, Integer> shoppingList = new HashMap<>();

        //get all the needed ingredients & quantities from the meal plans
        HashMap<String, Integer> neededIngredients = new HashMap<>();

        for (DataSnapshot snap : dataSnapshot.child("mealplan").getChildren()) {
            MealPlan mealPlan = snap.getValue(MealPlan.class);
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
                            int updatedQuantity = shoppingList.get(i) + mod.getQuantity();
                            if (updatedQuantity == 0) {
                                shoppingList.remove(i);
                            }
                            else {
                                shoppingList.put(i, updatedQuantity);
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
