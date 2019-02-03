package com.example.laure.thymesaver.UI.AddIngredients;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeIngredientActivity extends AppCompatActivity {
    public static String RECIPE_NAME = "My recipe name";
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private PantryViewModel mPantryViewModel;
    private Recipe mRecipe;
    private List<Ingredient> mTotalIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_ingredients);
        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mTotalIngredients = ingredients;
                List<String> names = new ArrayList<>();
                for (Ingredient i : ingredients) {
                    names.add(i.getName());
                }
                setUpIngredientNameTV(names);
            }
        });
        mRecipeDetailViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                mRecipe = recipe;
            }
        });
        setUpUnitTV();
    }

    private void setUpUnitTV() {
        final AutoCompleteTextView unitTV = findViewById(R.id.ingredient_unit);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.ingredient_units,
                android.R.layout.select_dialog_item);
        unitTV.setThreshold(0);
        unitTV.setAdapter(unitAdapter);
        unitTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                unitTV.showDropDown();
                return true;
            }
        });
    }

    private void setUpIngredientNameTV(List<String> ingredientNames) {
        final AutoCompleteTextView nameTV = findViewById(R.id.recipe_ingredient_name);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.select_dialog_item,
                ingredientNames);
        nameTV.setThreshold(1);
        nameTV.setAdapter(nameAdapter);
    }
}
