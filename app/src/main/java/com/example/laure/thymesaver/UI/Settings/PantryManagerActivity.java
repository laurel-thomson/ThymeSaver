package com.example.laure.thymesaver.UI.Settings;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.laure.thymesaver.Adapters.PantryListAdapter;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class PantryManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_manager);
        List<Pantry> pantries = new ArrayList<Pantry>();
        pantries.add(new Pantry("My Pantry", "1234", true, true));
        pantries.add(new Pantry("Alex Thomson's Pantry", "3334", false, false));

        PantryListAdapter adapter = new PantryListAdapter(this);
        adapter.setPantryList(pantries);

        RecyclerView mRecyclerView = findViewById(R.id.manage_pantry_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
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
