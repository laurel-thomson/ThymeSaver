package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;

import java.util.HashMap;
import java.util.List;

public class ShoppingViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<HashMap<String, Integer>> mShoppingList;

    public ShoppingViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mShoppingList = mRepository.getShoppingList();
    }

    public LiveData<HashMap<String, Integer>> getShoppingList() {
        return mShoppingList;
    }
}
