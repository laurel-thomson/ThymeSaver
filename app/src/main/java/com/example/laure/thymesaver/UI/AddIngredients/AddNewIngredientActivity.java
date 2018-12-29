package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

public class AddNewIngredientActivity extends AppCompatActivity {

    private PantryViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ingredient);
        setUpActionBar();

        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredient");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void saveIngredient() {
        EditText nameET = findViewById(R.id.ingredient_name_edittext);
        String name = nameET.getText().toString();

        EditText unitET = findViewById(R.id.ingredient_unit_edittext);
        String unit = unitET.getText().toString();

        EditText quantityET = findViewById(R.id.pantry_quantity_edittext);
        int quantity = Integer.parseInt(quantityET.getText().toString());

        Ingredient i = new Ingredient(name, unit, quantity);
        mViewModel.addIngredient(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveIngredient();
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_ingredient, menu);
        return true;
    }
}
