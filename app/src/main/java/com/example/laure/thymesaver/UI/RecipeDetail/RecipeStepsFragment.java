package com.example.laure.thymesaver.UI.RecipeDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.R;

public class RecipeStepsFragment extends RecipeDetailFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_steps, viewGroup, false);
    }

    @Override
    void launchAddItemActivity() {
        //not implemented
    }
}
