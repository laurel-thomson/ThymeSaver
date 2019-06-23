package com.example.laure.thymesaver.UI;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.AddPlannedMealsAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.CookBookViewModel;

import java.util.List;

public class AddPlannedMealsActivity extends AppCompatActivity {
    private AddPlannedMealsAdapter mAdapter;
    private CookBookViewModel mCookBookViewModel;
    private String mScheduledDay;
    public static String SCHEDULED_DAY = "Scheduled Day";
    private TextView mEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        final ProgressBar progressBar = findViewById(R.id.recycler_view_progress);
        mEmptyMessage = findViewById(R.id.empty_message);

        mScheduledDay = getIntent().getStringExtra(SCHEDULED_DAY);

        setUpActionBar();

        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));


        mAdapter = new AddPlannedMealsAdapter(this);

        mCookBookViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setTotalRecipes(recipes);
                progressBar.setVisibility(View.GONE);
            }
        });

        rv.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Scheduled Meals");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        addMealPlans();
        super.onBackPressed();
    }

    private void addMealPlans() {
        List<Recipe> plannedRecipes = mAdapter.getPlannedRecipes();
        mCookBookViewModel.addRecipesToMealPlan(plannedRecipes, mScheduledDay);
    }
}