package com.example.laure.thymesaver.Database.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseReferences {
    private static FirebaseDatabase dataBase;
    private static DatabaseReference pantryReference;
    private static DatabaseReference recipeReference;
    private static DatabaseReference ingredientReference;
    private static DatabaseReference mealPlanReference;
    private static DatabaseReference shoppingListModReference;
    private static DatabaseReference userReference;
    private static DatabaseReference pantriesReference;
    private static String pantryId;

    public static void initializeDatabaseReferences(String preferredPantry) {
        dataBase = FirebaseDatabase.getInstance();
        DatabaseReferences.pantryId = preferredPantry;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = dataBase.getReference("users/" + userId);
        pantryReference = dataBase.getReference("pantries/" + DatabaseReferences.pantryId);
        recipeReference = dataBase.getReference("pantries/" + DatabaseReferences.pantryId + "/recipes");
        ingredientReference = dataBase.getReference("pantries/" + DatabaseReferences.pantryId + "/ingredients");
        mealPlanReference = dataBase.getReference("pantries/" + DatabaseReferences.pantryId + "/mealplan");
        shoppingListModReference = dataBase.getReference("pantries/" + DatabaseReferences.pantryId + "/shoppinglistmods");
        pantriesReference = userReference.child("pantries");
    }

    public static DatabaseReference getPantryReference() {
        return pantryReference;
    }

    public static DatabaseReference getRecipeReference() {
        return recipeReference;
    }

    public static DatabaseReference getIngredientReference() {
        return ingredientReference;
    }

    public static DatabaseReference getMealPlanReference() {
        return mealPlanReference;
    }

    public static DatabaseReference getShoppingListModReference() {
        return shoppingListModReference;
    }

    public static DatabaseReference getUserReference() {
        return userReference;
    }

    public static DatabaseReference getPantriesReference() {
        return pantriesReference;
    }

    public static DatabaseReference getStarterPantryReference() {
        return dataBase.getReference("starterPantry");
    }

    public static String getPreferredPantry() {
        return pantryId;
    }
}
