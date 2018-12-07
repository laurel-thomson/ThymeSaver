package com.example.laure.thymesaver.UI;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.IngredientAdapter;
import com.example.laure.thymesaver.Adapters.RecipeAdapter;
import com.example.laure.thymesaver.Database.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class AddIngredients extends AppCompatActivity implements IngredientAdapter.IngredientAdapterListener {
    private IngredientAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add(new Ingredient("Mushrooms"));
        ingredients.add(new Ingredient("Potatoes"));

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
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {
        Toast.makeText(
                getApplicationContext(),
                "Selected: " + ingredient.getName(),
                Toast.LENGTH_LONG)
                    .show();
    }
}
