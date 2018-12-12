package com.example.laure.thymesaver.UI;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeViewModel;

public class AddEditRecipeActivity extends AppCompatActivity {
    private RecipeViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);
        mViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
    }

    public void createRecipeClick(View view) {
        EditText et = findViewById(R.id.recipe_name_edittext);
        String recipeName = et.getText().toString();
        Recipe r = new Recipe();
        r.setName(recipeName);
        mViewModel.addRecipe(r);
        finish();
    }

    public void addIngredientClick(View view) {
        Intent intent = new Intent(this, AddIngredientsActivity.class);
        startActivity(intent);
    }
}
