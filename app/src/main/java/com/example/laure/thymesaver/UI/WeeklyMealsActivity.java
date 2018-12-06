package com.example.laure.thymesaver.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.Database.Recipe;
import com.example.laure.thymesaver.Adapters.RecipeAdapter;

import java.util.ArrayList;

public class WeeklyMealsActivity extends AppCompatActivity {
    private ArrayList<Recipe> mRecipes;
    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_meals);

        initializeRecipes();
        mRecyclerView = findViewById(R.id.weekly_recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecipeAdapter(mRecipes);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initializeRecipes() {
        mRecipes = new ArrayList<Recipe>();
        mRecipes.add(new Recipe("Tikka Masala"));
        mRecipes.add(new Recipe("Veggie Soup"));

    }
}
