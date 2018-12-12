package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private List<Recipe> mQueryValuesList = new ArrayList<>();
    private final Query mQuery;
    private final MyEventListener mListener = new MyEventListener();

    public FirebaseQueryLiveData(Query query) {
        mQuery = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        mQuery = ref;
    }

    @Override
    protected void onActive() {
        mQuery.addChildEventListener(mListener);
    }

    @Override
    protected void onInactive() {
        mQuery.removeEventListener(mListener);
    }

    private class MyEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot != null){
                setValue(dataSnapshot);
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Recipe msg = snap.getValue(Recipe.class);
                    mQueryValuesList.add(msg);
                }
            } else {
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }

    }
}
