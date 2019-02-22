package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Switch;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

public class AddOrEditIngredientActivity extends AppCompatActivity {

    private PantryViewModel mViewModel;
    private AutoCompleteTextView mCategoryET;
    private EditText mNameET;
    private Switch mBulkSwitch;
    public static String INGREDIENT_NAME_KEY = "Ingredient Name Key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        setUpActionBar();
        setUpFields();
        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);

        String ingredientName = getIntent().getStringExtra(INGREDIENT_NAME_KEY);

        //if an ingredient name was passed in, then we need to all the user to edit the current ingredient
        if (ingredientName != null) {
            //get ingredient from pantry
            setIngredientValues(mViewModel.getIngredient(ingredientName));
            getSupportActionBar().setTitle("Edit Ingredient");
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredient");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpFields() {
        mNameET = findViewById(R.id.ingredient_name_edittext);
        mCategoryET = findViewById(R.id.ingredient_category);
        mBulkSwitch = findViewById(R.id.is_bulk_switch);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.ingredient_categories,
                android.R.layout.select_dialog_item);
        mCategoryET.setThreshold(0);
        mCategoryET.setAdapter(categoryAdapter);
        mCategoryET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mCategoryET.showDropDown();
                return true;
            }
        });
    }

    private void setIngredientValues(Ingredient ingredient) {
        mNameET.setText(ingredient.getName());
        mCategoryET.setText(ingredient.getCategory());
        mBulkSwitch.setChecked(ingredient.isBulk());
    }


    private void saveIngredient() {
        String name = mNameET.getText().toString();
        String category = mCategoryET.getText().toString();
        boolean isBulk = mBulkSwitch.isChecked();

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
