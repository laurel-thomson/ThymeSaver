package thomson.laurel.beth.thymesaver.Database.Firebase;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;

import thomson.laurel.beth.thymesaver.Database.Firebase.LiveData.ShoppingListLiveData;
import thomson.laurel.beth.thymesaver.Database.IShoppingRepository;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ModType;
import thomson.laurel.beth.thymesaver.Models.ShoppingListMod;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShoppingRepository implements IShoppingRepository {
    private static ShoppingRepository mSoleInstance;

    public static ShoppingRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new ShoppingRepository();
        }
        return mSoleInstance;
    }

    private ShoppingRepository() {
    }

    @Override
    public void addOrUpdateModification(ShoppingListMod mod) {
        DatabaseReferences.getShoppingListModReference().child(mod.getName()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ShoppingListMod oldMod = dataSnapshot.getValue(ShoppingListMod.class);
                            oldMod.setQuantity(oldMod.getQuantity() + mod.getQuantity());
                            DatabaseReferences.getShoppingListModReference().child(mod.getName()).setValue(oldMod);
                        }
                        else {
                            DatabaseReferences.getShoppingListModReference().child(mod.getName()).setValue(mod);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void updateModToChangeIfExists(String name) {
        DatabaseReferences.getShoppingListModReference().child(name).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //We only want to update the associated mod if it exists
                        if (dataSnapshot.exists()) {
                            ShoppingListMod mod = dataSnapshot.getValue(ShoppingListMod.class);
                            mod.setType(ModType.CHANGE);
                            mod.setQuantity(1);
                            DatabaseReferences.getShoppingListModReference().child(name).setValue(mod);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    @Override
    public void deleteShoppingModification(String name) {
        DatabaseReferences.getShoppingListModReference().child(name).removeValue();
    }

    @Override
    public void deleteAllModifications() {
        DatabaseReferences.getShoppingListModReference().removeValue();
    }

    @Override
    public void deleteShoppingListItem(Ingredient ingredient, int quantity) {
        DatabaseReferences.getShoppingListModReference().child(ingredient.getName()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            ShoppingListMod mod;
                            if (ingredient.isBulk()) {
                                mod = new ShoppingListMod(ingredient.getName(), ModType.DELETE, 0);
                            }
                            else {
                                mod = new ShoppingListMod(ingredient.getName(), ModType.CHANGE, 0 - quantity);
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

    @Override
    public LiveData<HashMap<Ingredient, Integer>> getShoppingList() {
        return Transformations.map(
                new ShoppingListLiveData(DatabaseReferences.getPantryReference()),
                new ShoppingListDeserializer());
    }

    @Override
    public void getShoppingList(ValueCallback<HashMap<Ingredient, Integer>> callback) {
        DatabaseReferences.getPantryReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onSuccess(ShoppingListLiveData.getShoppingList(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toString());
            }
        });
    }

    private class ShoppingListDeserializer implements Function<DataSnapshot, HashMap<Ingredient, Integer>> {

        @Override
        public HashMap<Ingredient, Integer> apply(DataSnapshot dataSnapshot) {
            return ShoppingListLiveData.getShoppingList(dataSnapshot);
        }
    }
}
