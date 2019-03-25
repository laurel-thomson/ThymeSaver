package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.Models.PantryRequest;

import java.util.List;

public class PantryManagerViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<Pantry>> mPantries;
    private LiveData<List<PantryRequest>> mPantryRequests;

    public PantryManagerViewModel(@NonNull Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mPantries = mRepository.getPantries();
        mPantryRequests = mRepository.getPantryRequests();
    }

    public LiveData<List<Pantry>> getPantries() {
        return mPantries;
    }

    public LiveData<List<PantryRequest>> getPantryRequests() {
        return mPantryRequests;
    }

    public void requestJoinPantry(String email) {
        mRepository.requestJoinPantry(email);
    }
}
