package thomson.laurel.beth.thymesaver.Database.Firebase.LiveData;

import androidx.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RecipeIngredientsLiveData extends LiveData<DataSnapshot> {
    private HashMap<Ingredient, RecipeQuantity> mRecipeIngredients = new HashMap<>();
    private final Query mQuery;
    private final String mRecipeName;
    private final RecipeIngredientsLiveData.MyEventListener mListener = new RecipeIngredientsLiveData.MyEventListener();

    public RecipeIngredientsLiveData(Query q, String recipeName) {
        mQuery = q;
        mRecipeName = recipeName;
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
                    mRecipeIngredients = getRecipeIngredients(dataSnapshot, mRecipeName);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public static HashMap<Ingredient, RecipeQuantity> getRecipeIngredients(DataSnapshot dataSnapshot, String recipeName) {
        DataSnapshot snap = dataSnapshot.child("recipes").child(recipeName);
        Recipe recipe = snap.getValue(Recipe.class);
        recipe.setName(snap.getKey());

        HashMap<Ingredient, RecipeQuantity> recipeIngredients = new HashMap<>();
        //add the regular ingredients in
        for (String ingName : recipe.getRecipeIngredients().keySet()) {
            snap = dataSnapshot.child("ingredients").child(ingName);
            Ingredient ing = snap.getValue(Ingredient.class);
            ing.setName(snap.getKey());
            recipeIngredients.put(ing, recipe.getRecipeIngredients().get(ingName));
        }

        //add the sub recipe ingredients in
        for (String subRecipeName : recipe.getSubRecipes()) {
            snap = dataSnapshot.child("recipes").child(subRecipeName);
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
