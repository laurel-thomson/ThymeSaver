package com.example.laure.thymesaver.Firebase.Database;

import android.arch.lifecycle.LiveData;
import com.example.laure.thymesaver.Models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecipeLiveData extends LiveData<DataSnapshot> {
    private Recipe mRecipe;
    private final Query mQuery;
    private final MyEventListener mListener = new MyEventListener();

    public RecipeLiveData (Query q) {
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

    private class MyEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null) {
                setValue(dataSnapshot);
                mRecipe = dataSnapshot.getValue(Recipe.class);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
