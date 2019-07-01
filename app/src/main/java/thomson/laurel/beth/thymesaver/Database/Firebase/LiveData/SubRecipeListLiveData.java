package thomson.laurel.beth.thymesaver.Database.Firebase.LiveData;

import android.arch.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SubRecipeListLiveData extends LiveData<DataSnapshot> {
    private List<Recipe> mSubRecipes = new ArrayList<>();
    private final Query mQuery;
    private final String mRecipeName;

    private final SubRecipeListLiveData.MyEventListener mListener = new SubRecipeListLiveData.MyEventListener();

    public SubRecipeListLiveData(Query q, String recipeName) {
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
                mSubRecipes = getAvailableSubRecipes(dataSnapshot, mRecipeName);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private static Recipe getRecipe(DataSnapshot snap) {
        Recipe recipe = snap.getValue(Recipe.class);
        recipe.setName(snap.getKey());
        return recipe;
    }

    public static List<Recipe> getAvailableSubRecipes(DataSnapshot dataSnapshot, String recipeName) {
        if (recipeName == null) return null;

        List<Recipe> allowedRecipes = new ArrayList<>();
        List<String> disallowedRecipes = new ArrayList<>();
        Recipe parent = getRecipe(dataSnapshot.child(recipeName));
        disallowedRecipes.add(parent.getName());
        disallowedRecipes.addAll(parent.getSubRecipes());

        for (DataSnapshot snap : dataSnapshot.getChildren()) {
            if (!disallowedRecipes.contains(snap.getKey())) {
                Recipe child = getRecipe(snap);
                //only recipes without sub recipes are allowed to themselves be sub recipes
                if (child.getSubRecipes() == null || child.getSubRecipes().size() == 0) {
                    allowedRecipes.add(child);
                }
            }
        }

        return allowedRecipes;
    }
}
