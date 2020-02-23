package thomson.laurel.beth.thymesaver.Database.Firebase;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.ListLiveData;
import thomson.laurel.beth.thymesaver.Database.IPantryRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ModType;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.ShoppingListMod;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PantryRepository implements IPantryRepository {
    private static PantryRepository mSoleInstance;
    private List<Ingredient> mIngredients = new ArrayList<>();

    public static PantryRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new PantryRepository();
        }
        return mSoleInstance;
    }

    private PantryRepository() {
    }

    @Override
    public void addIngredient(Ingredient i) {
        DatabaseReferences.getIngredientReference().child(i.getName()).setValue(i);
        DatabaseReferences.getShoppingListModReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    ShoppingListMod mod = snap.getValue(ShoppingListMod.class);
                    mod.setName(snap.getKey());
                    if (mod.getName().equals(i.getName()) && mod.getType() == ModType.NEW) {
                        mod.setType(ModType.ADD);
                        DatabaseReferences.getShoppingListModReference().child(mod.getName()).setValue(mod);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addImportedIngredient(Ingredient i, Callback cb) {
        DatabaseReferences.getIngredientReference().child(i.getName()).setValue(i)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    cb.onSuccess();
                }
            });
    }

    @Override
    public void deleteIngredient(Ingredient i) {
        DatabaseReferences.getRecipeReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Recipe recipe = snap.getValue(Recipe.class);
                    recipe.setName(snap.getKey());
                    if (recipe.getRecipeIngredients().containsKey(i.getName())) {
                        recipe.getRecipeIngredients().remove(i.getName());
                        CookbookRepository.getInstance().addOrUpdateRecipe(recipe);
                    }
                }
                DatabaseReferences.getIngredientReference().child(i.getName()).removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReferences.getShoppingListModReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    ShoppingListMod mod = snap.getValue(ShoppingListMod.class);
                    mod.setName(snap.getKey());
                    if (mod.getName().equals(i.getName())) {
                        DatabaseReferences.getShoppingListModReference().child(mod.getName()).removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateIngredient(Ingredient i) {
        DatabaseReferences.getIngredientReference().child(i.getName()).setValue(i);
    }

    @Override
    public void getIngredient(String ingredientName, ValueCallback<Ingredient> callback) {
        DatabaseReferences.getIngredientReference().child(ingredientName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            callback.onError("The ingredient does not exist");
                            return;
                        }
                        Ingredient ing = dataSnapshot.getValue(Ingredient.class);
                        ing.setName(dataSnapshot.getKey());
                        callback.onSuccess(ing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.toString());
                    }
                });
    }

    @Override
    public void getAllIngredients(ValueCallback<List<Ingredient>> callback) {
        DatabaseReferences.getIngredientReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mIngredients.clear();

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Ingredient i = snap.getValue(Ingredient.class);
                    i.setName(snap.getKey());
                    mIngredients.add(i);
                }
                callback.onSuccess(mIngredients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toString());
            }
        });
    }

    @Override
    public LiveData<List<Ingredient>> getAllIngredients() {
        return Transformations.map(
                new ListLiveData<Ingredient>(DatabaseReferences.getIngredientReference(), Ingredient.class),
                new IngredientDeserializer());
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
}
