package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.Repository.IPantryManagerRepository;
import com.example.laure.thymesaver.Firebase.Database.Repository.PantryManagerRepository;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.UI.Callbacks.Callback;

import java.util.List;

public class PantryManagerViewModel extends AndroidViewModel {
    private IPantryManagerRepository mRepository;
    private LiveData<List<Pantry>> mPantries;
    private LiveData<List<PantryRequest>> mPantryRequests;

    public PantryManagerViewModel(@NonNull Application application) {
        super(application);
        mRepository = PantryManagerRepository.getInstance();
        mPantries = mRepository.getPantries();
        mPantryRequests = mRepository.getPantryRequests();
    }

    public LiveData<List<Pantry>> getPantries() {
        return mPantries;
    }

    public LiveData<List<PantryRequest>> getPantryRequests() {
        return mPantryRequests;
    }

    public void requestJoinPantry(String email, Callback callBack) {
        mRepository.trySendJoinPantryRequest(email, callBack);
    }

    public void acceptJoinRequest(PantryRequest request) {
        mRepository.acceptJoinRequest(request);
    }

    public void declineJoinRequest(PantryRequest request) {
        mRepository.declineJoinRequest(request);
    }

    public void updatePreferredPantry(String pantryId) {
        mRepository.updatePreferredPantry(pantryId);
    }

    public String getPreferredPantryId() {
        return mRepository.getPreferredPantryId();
    }

    public void leavePantry(Pantry pantry) {
        mRepository.leavePantry(pantry);
    }
}
