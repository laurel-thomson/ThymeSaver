package thomson.laurel.beth.thymesaver.Database.Firebase.LiveData;

import android.arch.lifecycle.LiveData;

import thomson.laurel.beth.thymesaver.Database.Firebase.ShoppingRepository;
import thomson.laurel.beth.thymesaver.Models.BulkIngredientState;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.ShoppingListMod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

    private static Recipe getRecipe(DataSnapshot snap) {
        Recipe recipe = snap.getValue(Recipe.class);
        recipe.setName(snap.getKey());
        return recipe;
    }

    private static Ingredient getIngredient(DataSnapshot snap) {
        Ingredient ing = snap.getValue(Ingredient.class);
        ing.setName(snap.getKey());
        return ing;
    }

    private static ShoppingListMod getMod(DataSnapshot snap) {
        ShoppingListMod mod = snap.getValue(ShoppingListMod.class);
        mod.setName(snap.getKey());
        return mod;
    }

    private static void addIngredientQuantities(HashMap<String, Double> neededIngredients, Recipe recipe) {
        for (String ingName : recipe.getRecipeIngredients().keySet()) {
            double quantity = recipe.getRecipeIngredients().get(ingName).getRecipeQuantity();
            if (neededIngredients.containsKey(ingName)) {
                double oldQuantity = neededIngredients.get(ingName);
                neededIngredients.put(ingName, oldQuantity + quantity);
            }
            else {
                neededIngredients.put(ingName, quantity);
            }
        }
    }

    private static void removeIngredientFromName(HashMap<Ingredient, Integer> shoppingList, String ingName) {
        Ingredient matchingIng = null;
        for (Ingredient ing : shoppingList.keySet()) {
            if (ing.getName().equals(ingName)) {
                matchingIng = ing;
                break;
            }
        }
        if (matchingIng != null) {
            shoppingList.remove(matchingIng);
        }
    }

    private static Ingredient getIngredientFromList(HashMap<Ingredient, Integer> shoppingList, String ingName) {
        for (Ingredient ing : shoppingList.keySet()) {
            if (ing.getName().equals(ingName)) {
                return ing;
            }
        }
        return null;
    }

    private static void deleteModification(ShoppingListMod mod)
    {
        ShoppingRepository.getInstance().deleteShoppingModification(mod.getName());
    }

    public static HashMap<Ingredient, Integer> getShoppingList(DataSnapshot dataSnapshot) {
        HashMap<Ingredient, Integer> shoppingList = new HashMap<>();
        HashMap<String, Double> neededIngredients = new HashMap<>();

        //get the needed ingredients from the meal plans
        for (DataSnapshot snap : dataSnapshot.child("mealplan").getChildren()) {
            String recipeName = snap.getValue(MealPlan.class).getRecipeName();
            Recipe recipe = getRecipe(dataSnapshot.child("recipes").child(recipeName));

            //get regular ingredients
            addIngredientQuantities(neededIngredients, recipe);

            //get subrecipe ingredients
            for (String subRecipeName : recipe.getSubRecipes()) {
                Recipe subRecipe = getRecipe(dataSnapshot.child("recipes").child(subRecipeName));
                addIngredientQuantities(neededIngredients, subRecipe);
            }
        }

        //Subtract away from the shopping list what we already have in the pantry
        for (String ingName : neededIngredients.keySet()) {
            Ingredient ingredient = getIngredient(dataSnapshot.child("ingredients").child(ingName));
            if (!ingredient.isBulk()) {
                int neededQuantity = (int) Math.ceil(neededIngredients.get(ingName));
                int updatedNeededQuantity = neededQuantity - ingredient.getQuantity();
                if (updatedNeededQuantity > 0) {
                    shoppingList.put(ingredient, updatedNeededQuantity);
                }
            }
            else if (ingredient.getQuantity() != BulkIngredientState.convertEnumToInt(BulkIngredientState.IN_STOCK)){
                shoppingList.put(ingredient, 1);
            }
        }

        //Add in the shopping list modifications
        for (DataSnapshot snap : dataSnapshot.child("shoppinglistmods").getChildren()) {
            ShoppingListMod mod = getMod(snap);
            switch (mod.getType()) {
                case ADD:
                    Ingredient ing = getIngredient(dataSnapshot.child("ingredients").child(mod.getName()));
                    shoppingList.put(ing, 0);
                    break;
                case DELETE:
                    removeIngredientFromName(shoppingList, mod.getName());
                    break;
                case NEW:
                    shoppingList.put(new Ingredient(mod.getName(), "Misc", false), mod.getQuantity());
                    break;
                case CHANGE:
                    Ingredient matchingIng = getIngredientFromList(shoppingList, mod.getName());
                    if (matchingIng != null) {
                        int updatedQuantity = shoppingList.get(matchingIng) + mod.getQuantity();
                        if (updatedQuantity == 0) {
                            shoppingList.remove(matchingIng);
                        }
                        else if (updatedQuantity < 0)
                        {
                            shoppingList.remove(matchingIng);
                            deleteModification(mod);
                        }
                        else {
                            shoppingList.put(matchingIng, updatedQuantity);
                        }
                    }
                    else {
                        if (mod.getQuantity() > 0) {
                            Ingredient i = getIngredient(dataSnapshot.child("ingredients").child(mod.getName()));
                            shoppingList.put(i, mod.getQuantity());
                        }
                        else {
                            deleteModification(mod);
                        }
                    }
            }
        }
        return shoppingList;
    }
}
