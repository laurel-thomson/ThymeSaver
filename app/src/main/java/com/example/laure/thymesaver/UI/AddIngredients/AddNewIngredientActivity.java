package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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
        setUpAutoCompleteTVs();
        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
    }

    private void setUpAutoCompleteTVs() {
        final AutoCompleteTextView categoryTV = findViewById(R.id.ingredient_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.ingredient_categories,
                android.R.layout.select_dialog_item);
        categoryTV.setThreshold(0);
        categoryTV.setAdapter(categoryAdapter);
        categoryTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                categoryTV.showDropDown();
                return true;
            }
        });
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

        AutoCompleteTextView categoryTV = findViewById(R.id.ingredient_category);
        String category = categoryTV.getText().toString();

        Switch bulkSwitch = findViewById(R.id.is_bulk_switch);
        boolean isBulk = bulkSwitch.isChecked();

        Ingredient i = new Ingredient(name, category, isBulk);
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
