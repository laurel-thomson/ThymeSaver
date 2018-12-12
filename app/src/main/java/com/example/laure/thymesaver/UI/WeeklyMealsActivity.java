package com.example.laure.thymesaver.UI;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.laure.thymesaver.Adapters.RecipeAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeViewModel;
import java.util.List;

public class WeeklyMealsActivity extends AppCompatActivity {
    private RecipeViewModel mViewModel;
    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_meals);
        mViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        mViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                //update the cached copy of recipes in the adapter
                mAdapter.setRecipes(recipes);
            }
        });

        mRecyclerView = findViewById(R.id.weekly_recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addRecipeClick(View view) {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        startActivity(intent);
    }
}
