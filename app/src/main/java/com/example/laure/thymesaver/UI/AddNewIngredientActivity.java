package com.example.laure.thymesaver.UI;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.laure.thymesaver.R;

public class AddNewIngredientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ingredient);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Ingredient");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                Toast.makeText(this, "Save clicked!", Toast.LENGTH_SHORT).show();
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
