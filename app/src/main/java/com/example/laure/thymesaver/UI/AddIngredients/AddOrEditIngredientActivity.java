package com.example.laure.thymesaver.UI.AddIngredients;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.Switch;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.Callbacks.IngredientCallback;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

public class AddOrEditIngredientActivity extends AppCompatActivity {

    private PantryViewModel mViewModel;
    private AutoCompleteTextView mCategoryET;
    private EditText mNameET;
    private Switch mBulkSwitch;
    private Ingredient mIngredient;
    private ProgressBar mProgressBar;
    public static String INGREDIENT_NAME_KEY = "Ingredient Name Key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        setUpActionBar();
        setUpFields();

        mProgressBar = findViewById(R.id.add_edit_ingredient_progress);
        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);

        String ingredientName = getIntent().getStringExtra(INGREDIENT_NAME_KEY);

        //if an ingredient name was passed in, then we need to all the user to edit the current ingredient
        if (ingredientName != null) {
            mViewModel.getIngredient(ingredientName, new IngredientCallback() {
                @Override
                public void onSuccess(Ingredient ingredient) {
                    mIngredient = ingredient;
                    setIngredientValues();
                    getSupportActionBar().setTitle("Edit Ingredient");
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(String error) {

                }
            });

        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredient");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpFields() {
        mNameET = findViewById(R.id.ingredient_name_edittext);
        mCategoryET = findViewById(R.id.ingredient_category);
        mBulkSwitch = findViewById(R.id.is_bulk_switch);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.ingredient_categories,
                android.R.layout.select_dialog_item);
        mCategoryET.setAdapter(categoryAdapter);
        mCategoryET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Need to repopulate the adapter because if setText() has been called, the array resources
                //will have been cleared
                ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                        getApplicationContext(),
                        R.array.ingredient_categories,
                        android.R.layout.select_dialog_item);
                mCategoryET.setAdapter(categoryAdapter);
                mCategoryET.showDropDown();
                return true;
            }
        });
    }

    private void setIngredientValues() {
        if (mIngredient == null) return;

        mNameET.setText(mIngredient.getName());
        mBulkSwitch.setChecked(mIngredient.isBulk());

        CharSequence[] categories = getResources().getTextArray(R.array.ingredient_categories);
        for (CharSequence cs : categories) {
            if (cs.toString().equals(mIngredient.getCategory())) {
                mCategoryET.setText(cs);
            }
        }
    }


    private void saveIngredient() {
        String name = mNameET.getText().toString();
        String category = mCategoryET.getText().toString();
        boolean isBulk = mBulkSwitch.isChecked();

        Ingredient ingredient;
        if (mIngredient == null) ingredient =  new Ingredient(name, category, isBulk);
        else {
            ingredient = mIngredient;
            ingredient.setName(name);
            ingredient.setCategory(category);

            //if we are switching a bulk ingredient to a non-bulk, the associated shopping mod will
            //need to be update if it exists to prevent bugs in the shopping list
            if (ingredient.isBulk() && !isBulk) {
                mViewModel.updateModToChange(ingredient.getName());
            }
            ingredient.setBulk(isBulk);
        }
        mViewModel.addIngredient(ingredient);
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
