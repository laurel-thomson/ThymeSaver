package com.example.laure.thymesaver.Database.Firebase;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Database.Firebase.LiveData.ListLiveData;
import com.example.laure.thymesaver.Database.IPantryManagerRepository;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.Follower;
import com.example.laure.thymesaver.UI.Callbacks.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PantryManagerRepository implements IPantryManagerRepository {
    private static PantryManagerRepository mSoleInstance;
    private List<Follower> mFollowers = new ArrayList<>();
    private List<Pantry> mPantries = new ArrayList<>();


    public static PantryManagerRepository getInstance() {
        if (mSoleInstance == null) {
            mSoleInstance = new PantryManagerRepository();
        }
        return mSoleInstance;
    }

    private PantryManagerRepository() {
        //todo: figure out if we need to put this back
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void initializePreferredPantry(Callback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users/" + userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pantryId;
                if (dataSnapshot.hasChild("preferredPantry")) {
                    pantryId = dataSnapshot.child("preferredPantry").getValue().toString();
                    DatabaseReferences.initializeDatabaseReferences(pantryId);
                }
                else {
                    //the default pantry id is just the user's own pantry (which is the user's id)
                    DatabaseReferences.initializeDatabaseReferences(userId);
                    //If the preferred pantry doesn't exist, then this is a new user & we need
                    //to initialize the pantry
                    initializeNewPantry();
                }
                callback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("There was a problem with the database.");
            }
        });
    }

    private void initializeNewPantry() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReferences.getUserReference().child("preferredPantry").setValue(userId);
        DatabaseReferences.getPantriesReference().child(userId).setValue(new Pantry("My Pantry", true));
        DatabaseReferences.getUserReference().child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //Populate pantry with starter pantry items
        DatabaseReferences.getStarterPantryReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReferences.getIngredientReference().setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updatePreferredPantry(String pantryId) {
        DatabaseReferences.getUserReference().child("preferredPantry").setValue(pantryId);
        DatabaseReferences.initializeDatabaseReferences(pantryId);
    }

    @Override
    public void trySendJoinPantryRequest(String email, Callback callBack) {
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(email).limitToFirst(1)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Follower request = new Follower(user.getDisplayName(), user.getEmail());

                                    String requestedUser = "";

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        //there will only ever be one child (the requested user)
                                        requestedUser = child.getKey();
                                    }

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("users")
                                            .child(requestedUser)
                                            .child("requests")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(request);
                                    callBack.onSuccess();
                                }
                                else {
                                    callBack.onError("The requested user does not exist.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callBack.onError("There was an error processing your request.");
                            }
                        }
                );
    }

    @Override
    public void acceptJoinRequest(Follower follower) {
        DatabaseReferences.getUserReference().child("requests").child(follower.getuID()).removeValue();

        DatabaseReferences.getUserReference().child("pantries")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Pantry pantry = dataSnapshot.getValue(Pantry.class);
                        pantry.getFollowers().add(follower);
                        DatabaseReferences.getUserReference().child("pantries")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(pantry);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //add current user's pantry to requesting users list of pantries
        FirebaseDatabase.getInstance().getReference().child("users").child(follower.getuID()).child("pantries")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(new Pantry(
                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        false));
    }

    @Override
    public void declineJoinRequest(Follower request) {
        DatabaseReferences.getUserReference().child("requests").child(request.getuID()).removeValue();
    }

    @Override
    public void leavePantry(Pantry pantry) {
        DatabaseReferences.getPantriesReference().child(pantry.getuId()).removeValue();
        FirebaseDatabase.getInstance().getReference("users").child(pantry.getuId()).child("acceptedRequests")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        if (pantry.getuId().equals(DatabaseReferences.getPreferredPantry())) {
            DatabaseReferences.getUserReference().child("preferredPantry").setValue(
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
    }

    @Override
    public void removeFollower(Follower follower) {
        DatabaseReferences.getUserReference().child("pantries")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Pantry pantry = dataSnapshot.getValue(Pantry.class);

                        for (Follower f : pantry.getFollowers()) {
                            if (f.getuID().equals(follower.getuID())) {
                                pantry.getFollowers().remove(f);
                                removeMyPantryFromUser(f.getuID());
                                break;
                            }
                        }

                        DatabaseReferences.getUserReference().child("pantries")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(pantry);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void removeMyPantryFromUser(String userID) {
        FirebaseDatabase.getInstance().getReference("users").child(userID)
                .child("pantries").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        //todo: if they have no more followers, need to make sure their preferred pantry is their own pantry
    }

    @Override
    public String getPreferredPantryId() {
        return DatabaseReferences.getPreferredPantry();
    }

    @Override
    public LiveData<List<Pantry>> getPantries() {
        return Transformations.map(
                new ListLiveData<Pantry>(DatabaseReferences.getPantriesReference(), Pantry.class),
                new PantryListDeserializer());
    }

    @Override
    public LiveData<List<Follower>> getFollowers() {
        return Transformations.map(
                new ListLiveData<Follower>(DatabaseReferences.getUserReference().child("requests"), Follower.class),
                new PantryRequestsDeserializer());
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

    private class PantryRequestsDeserializer implements Function<DataSnapshot, List<Follower>> {

        @Override
        public List<Follower> apply(DataSnapshot input) {
            mFollowers.clear();

            for (DataSnapshot snap : input.getChildren()) {
                Follower request = snap.getValue(Follower.class);
                request.setuID(snap.getKey());
                mFollowers.add(request);
            }

            return mFollowers;
        }
    }
}
