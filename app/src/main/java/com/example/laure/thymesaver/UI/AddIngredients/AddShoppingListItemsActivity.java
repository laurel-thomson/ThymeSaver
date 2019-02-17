package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import com.example.laure.thymesaver.R;
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
            mShoppingViewModel.addShoppingModification(entry.getKey().getName(), entry.getValue());
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredients");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredient, menu);
        return true;
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

                //create a new HashMap that includes all of the ingredients in the pantry
                //the value is 0 if the ingredient is not in the shopping list, and nonzero otherwise
                HashMap<Ingredient, Integer> totalMeasuredIngredients = new HashMap<>();
                HashMap<Ingredient, Integer> shoppingList = mShoppingViewModel.getShoppingList().getValue();
                for (Ingredient i : ingredients) {
                    if (shoppingList.containsKey(i.getName())) {
                        totalMeasuredIngredients.put(i, shoppingList.get(i.getName()));
                    }
                    else {
                        totalMeasuredIngredients.put(i, 0);
                    }
                }
                mAdapter.setIngredients(totalMeasuredIngredients);
            }
        });
    }
}
