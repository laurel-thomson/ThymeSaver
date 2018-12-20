package com.example.laure.thymesaver.UI;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.IngredientAdapter;
import com.example.laure.thymesaver.Adapters.MultiselectIngredientAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AddIngredientsActivity extends AppCompatActivity implements IngredientAdapter.IngredientAdapterListener {

    private IngredientAdapter mAdapter;
    private SearchView mSearchView;
    private PantryViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        setUpActionBar();

        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        RecyclerView rv = findViewById(R.id.ingredient_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MultiselectIngredientAdapter(this,this);
        mViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                //update the cached copy of ingredients in the adapter
                mAdapter.setIngredients(ingredients);
            }
        });
        rv.setAdapter(mAdapter);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Recipe Ingredients");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredient, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case android.R.id.home:
                //todo: save recipes ingredients
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient ingredient, int quantity) {

        //todo: update this method...

        Toast.makeText(
                getApplicationContext(),
                ingredient.getName(),
                Toast.LENGTH_SHORT)
                    .show();
    }
}
