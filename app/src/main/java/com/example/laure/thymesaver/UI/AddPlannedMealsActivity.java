package com.example.laure.thymesaver.UI;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.AddPlannedMealsAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.CookBookViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddPlannedMealsActivity extends AppCompatActivity {
    private AddPlannedMealsAdapter mAdapter;
    private CookBookViewModel mCookBookViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planned_meals);
        ProgressBar progressBar = findViewById(R.id.add_planned_meals_progress);

        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        RecyclerView rv = findViewById(R.id.planned_meals_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));

        //dummy lists for testing
        List<Recipe> totalRecipes = new ArrayList<>();
        totalRecipes.add(new Recipe("soup"));
        List<Recipe> plannedRecipes = new ArrayList<>();
        plannedRecipes.add(new Recipe("soup"));

        mAdapter = new AddPlannedMealsAdapter(this, totalRecipes, plannedRecipes);
        rv.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
    }
}