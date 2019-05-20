package com.example.laure.thymesaver.Firebase.Database.Repository;

import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.UI.Callbacks.Callback;

import java.util.List;

public interface IPantryManagerRepository {
    void initializePreferredPantry(Callback callback);

    void updatePreferredPantry(String pantryId);

    void trySendJoinPantryRequest(String email, Callback callBack);

    void acceptJoinRequest(PantryRequest request);

    void declineJoinRequest(PantryRequest request);

    void leavePantry(Pantry pantry);

    String getPreferredPantryId();

    LiveData<List<Pantry>> getPantries();

    LiveData<List<PantryRequest>> getPantryRequests();
}
