package com.example.laure.thymesaver.Firebase.Database;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.Models.ShoppingListMod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mRecipeReference;
    private DatabaseReference mIngredientReference;
    private DatabaseReference mMealPlanReference;
    private DatabaseReference mShoppingListModReference;
    private DatabaseReference mUserReference;
    private DatabaseReference mPantriesReference;
    private static Repository mSoleInstance;
    private List<Recipe> mRecipes = new ArrayList<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<MealPlan> mMealPlans = new ArrayList<>();
    private List<PantryRequest> mPantryRequests = new ArrayList<>();
    private HashMap<Ingredient, Integer> mShoppingList = new HashMap<>();
    private List<Pantry> mPantries = new ArrayList<>();
    private LiveData<List<Recipe>> mRecipeListLiveData;
    private LiveData<List<Ingredient>> mIngredientLiveData;
    private LiveData<List<MealPlan>> mMealPlanLiveData;
    private LiveData<List<PantryRequest>> mRequestsLiveData;
    private LiveData<HashMap<Ingredient,Integer>> mShoppingLiveData;
    private LiveData<Recipe> mRecipeLiveData;
    private LiveData<List<Pantry>> mPantryListLiveData;
    private Recipe mRecipe;
    private String mUserId;
    private String mPantryId;

    public static Repository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new Repository();
        }
        return mSoleInstance;
    }

    private Repository() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void initializePantry() {
        mPantryId = mUserId;
        mUserReference.child("preferredPantry").setValue(mUserId);
        mPantriesReference.child(mUserId).setValue(new Pantry("My Pantry", true));
        mUserReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //TODO: populate pantry with generic items
    }

    public void updatePreferredPantry(String pantryId) {
        mPantryId = pantryId;
        mUserReference.child("preferredPantry").setValue(pantryId);
        initializeDatabaseReferences(pantryId);
    }

    public void initializeDatabaseReferences(String pantryId) {
        mPantryId = pantryId;
        mUserReference = mDatabase.getReference("users/" + mUserId);
        mDatabaseReference = mDatabase.getReference("pantries/" + mPantryId);
        mRecipeReference = mDatabase.getReference("pantries/" + mPantryId + "/recipes");
        mIngredientReference = mDatabase.getReference("pantries/" + mPantryId + "/ingredients");
        mMealPlanReference = mDatabase.getReference("pantries/" + mPantryId + "/mealplan");
        mShoppingListModReference = mDatabase.getReference("pantries/" + mPantryId + "/shoppinglistmods");
        mPantriesReference = mUserReference.child("pantries");
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
                new ShoppingListLiveData(mDatabaseReference),
                new ShoppingListDeserializer());
        mPantryListLiveData = Transformations.map(
                new ListLiveData<Pantry>(mPantriesReference, Pantry.class),
                new PantryListDeserializer());
        mRequestsLiveData = Transformations.map(
                new ListLiveData<PantryRequest>(mUserReference.child("requests"), PantryRequest.class),
                new PantryRequestsDeserializer());
    }

    public void acceptJoinRequest(PantryRequest request) {
        mUserReference.child("requests").child(request.getuID()).removeValue();

        //add new user to current user's list of accepted requests
        mUserReference.child("acceptedRequests").child(request.getuID()).setValue(request);

        //add current user's pantry to requesting users list of pantries
        mDatabase.getReference().child("users").child(request.getuID()).child("pantries")
                .child(mUserId).setValue(
                        new Pantry(
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                false));
    }

    public void declineJoinRequest(PantryRequest request) {
        mUserReference.child("requests").child(request.getuID()).removeValue();
    }

    public void requestJoinPantry(final String requestEmail) {
        mDatabase.getReference().child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                String email = snap.child("email").getValue().toString();
                                if (email.equals(requestEmail)) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    PantryRequest request = new PantryRequest(user.getDisplayName(), user.getEmail());
                                    mDatabase.getReference().child("users").child(snap.getKey()).child("requests").child(mUserId)
                                            .setValue(request);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public String getPreferredPantryId() { return mPantryId; }

    public LiveData<HashMap<Ingredient, RecipeQuantity>> getRecipeIngredients(Recipe r) {
        mRecipe = r;
        return Transformations.map(
                        new RecipeIngredientsLiveData(mDatabaseReference, r),
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
        mShoppingListModReference.child(mod.getName()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ShoppingListMod oldMod = dataSnapshot.getValue(ShoppingListMod.class);
                            oldMod.setQuantity(oldMod.getQuantity() + mod.getQuantity());
                            mShoppingListModReference.child(mod.getName()).setValue(oldMod);
                        }
                        else {
                            mShoppingListModReference.child(mod.getName()).setValue(mod);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public void deleteShoppingModification(final String name) {
        mShoppingListModReference.child(name).removeValue();
    }

    public void deleteAllModifications() {
        mShoppingListModReference.removeValue();
    }

    public void deleteShoppingListItem(final Ingredient ingredient, final int quantity) {
        mShoppingListModReference.child(ingredient.getName()).addListenerForSingleValueEvent(
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

    @NonNull
    public LiveData<List<Pantry>> getPantries() {
        return mPantryListLiveData;
    }

    @NonNull
    public LiveData<List<PantryRequest>> getPantryRequests() {
        return mRequestsLiveData;
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

    private class PantryListDeserializer implements Function<DataSnapshot, List<Pantry>> {

        @Override
        public List<Pantry> apply(DataSnapshot input) {
            mPantries.clear();

            for(DataSnapshot snap : input.getChildren()) {
                Pantry pantry = snap.getValue(Pantry.class);
                pantry.setuId(snap.getKey());
                mPantries.add(pantry);
            }
            return mPantries;
        }
    }

    private class PantryRequestsDeserializer implements Function<DataSnapshot, List<PantryRequest>> {

        @Override
        public List<PantryRequest> apply(DataSnapshot input) {
            mPantryRequests.clear();

            for (DataSnapshot snap : input.getChildren()) {
                PantryRequest request = snap.getValue(PantryRequest.class);
                request.setuID(snap.getKey());
                mPantryRequests.add(request);
            }

            return mPantryRequests;
        }
    }

    private class RecipeIngredientsDeserializer implements Function<DataSnapshot, HashMap<Ingredient, RecipeQuantity>> {

        @Override
        public HashMap<Ingredient, RecipeQuantity> apply(DataSnapshot dataSnapshot) {
            return RecipeIngredientsLiveData.getRecipeIngredients(dataSnapshot, mRecipe);
        }
    }

    private class ShoppingListDeserializer implements  Function<DataSnapshot, HashMap<Ingredient, Integer>> {

        @Override
        public HashMap<Ingredient, Integer> apply(DataSnapshot dataSnapshot) {
            mShoppingList = ShoppingListLiveData.getShoppingList(dataSnapshot);
            return mShoppingList;
        }
    }
}
