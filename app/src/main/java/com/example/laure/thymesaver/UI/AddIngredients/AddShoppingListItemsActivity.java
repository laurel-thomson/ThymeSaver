package com.example.laure.thymesaver.UI.AddIngredients;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.AddShoppingItemsAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddShoppingListItemsActivity extends AppCompatActivity {
    private ShoppingViewModel mShoppingViewModel;
    private PantryViewModel mPantryViewModel;
    private AddShoppingItemsAdapter mAdapter;
    private SearchView mSearchView;
    //create a new HashMap that includes all of the ingredients in the pantry
    //the value is 0 if the ingredient is not in the shopping list, and nonzero otherwise
    HashMap<Ingredient, Integer> mTotalIngredients = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        setUpActionBar();
        mShoppingViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        RecyclerView rv = findViewById(R.id.ingredient_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AddShoppingItemsAdapter(this);
        setAdapterIngredients();
        rv.setAdapter(mAdapter);
    }

    public void saveIngredients() {
        for (Map.Entry<Ingredient, Integer> entry : mAdapter.getMeasuredIngredients().entrySet()) {
            Ingredient i = entry.getKey();
            if (isNewIngredient(i)) {
                mShoppingViewModel.addShoppingModification(i.getName(), ModType.NEW, entry.getValue());
            }
            else if (i.isBulk()) {
                mShoppingViewModel.addShoppingModification(i.getName(), ModType.ADD, 0);
            }
            else {
                mShoppingViewModel.addShoppingModification(i.getName(), ModType.CHANGE, entry.getValue());
            }
        }
    }

    private boolean isNewIngredient(Ingredient newIng) {
        for (Ingredient i : mTotalIngredients.keySet()) {
            if (i.getName().equals(newIng.getName())) {
                return false;
            }
        }
        return true;
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredients");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case android.R.id.home:
                saveIngredients();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAdapterIngredients() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients == null) return;


                mShoppingViewModel.getShoppingList(new ValueCallback<HashMap<Ingredient, Integer>>() {
                    @Override
                    public void onSuccess(HashMap<Ingredient, Integer> shoppingList) {
                        for (Ingredient i : ingredients) {
                            if (shoppingList.containsKey(i.getName())) {
                                mTotalIngredients.put(i, shoppingList.get(i.getName()));
                            }
                            else {
                                mTotalIngredients.put(i, 0);
                            }
                        }
                        mAdapter.setIngredients(mTotalIngredients);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
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
}
