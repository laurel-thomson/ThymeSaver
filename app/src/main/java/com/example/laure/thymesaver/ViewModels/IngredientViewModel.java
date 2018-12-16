package com.example.laure.thymesaver.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;

import java.util.List;

public class IngredientViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<Ingredient>> mIngredients;

    public IngredientViewModel(Application application) {
        super(application);
        mRepository = Repository.getInstance();
        mIngredients = mRepository.getAllIngredients();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return mIngredients;
    }

    public void addIngredient(Ingredient i) {
        mRepository.addIngredient(i);
    }
}
