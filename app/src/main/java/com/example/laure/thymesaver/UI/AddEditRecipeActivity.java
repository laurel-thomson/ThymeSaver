package com.example.laure.thymesaver.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.laure.thymesaver.R;

import java.util.Hashtable;

public class AddEditRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);
    }

    public void addIngredientClick(View view) {
        Intent intent = new Intent(this, AddIngredientsActivity.class);
        startActivity(intent);
    }
}
