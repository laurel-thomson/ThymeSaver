package com.example.laure.thymesaver.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.IngredientAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AddIngredientsActivity extends AppCompatActivity implements IngredientAdapter.IngredientAdapterListener {

    private IngredientAdapter mAdapter;
    private SearchView mSearchView;
    private Hashtable<Ingredient, Integer>  mRecipeIngredients = new Hashtable<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        RecyclerView rv = findViewById(R.id.ingredient_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IngredientAdapter(this, ingredients, this);
        rv.setAdapter(mAdapter);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {

        //either add the ingredient to the dictionary, or increment its quantity
        if (mRecipeIngredients.containsKey(ingredient)) {
            mRecipeIngredients.put(ingredient, mRecipeIngredients.get(ingredient) + 1);
        }
        else {
            mRecipeIngredients.put(ingredient, 1);
        }

        Toast.makeText(
                getApplicationContext(),
                ingredient.getName() + " recipe quantity = " +  mRecipeIngredients.get(ingredient),
                Toast.LENGTH_SHORT)
                    .show();
    }
}
