package com.example.laure.thymesaver.UI.Settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.PantryListAdapter;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryManagerViewModel;

import java.util.ArrayList;
import java.util.List;

public class PantryManagerActivity extends AppCompatActivity {
    private PantryManagerViewModel mViewModel;
    private PantryListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_manager);
        final ProgressBar progressBar = findViewById(R.id.pantry_manager_progress);

        mViewModel = ViewModelProviders.of(this).get(PantryManagerViewModel.class);
        mAdapter = new PantryListAdapter(this);
        mRecyclerView = findViewById(R.id.manage_pantry_rv);
        mRecyclerView.setVisibility(View.GONE);

        mViewModel.getPantries().observe(this, new Observer<List<Pantry>>() {
            @Override
            public void onChanged(@Nullable List<Pantry> pantries) {
                mAdapter.setPantryList(pantries);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        setUpActionBar();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Manage Pantries");
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

}
