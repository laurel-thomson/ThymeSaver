package com.example.laure.thymesaver.UI.TopLevel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.laure.thymesaver.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new MealPlannerFragment());
        transaction.commit();

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Fragment fragment = null;
                        switch(menuItem.getItemId())
                        {
                            case R.id.action_meal_planner:
                                fragment = new MealPlannerFragment();
                                break;
                            case R.id.action_cookbook:
                                fragment = new CookbookFragment();
                                break;
                            case R.id.action_pantry:
                                fragment = new PantryFragment();
                                break;
                            case R.id.action_shopping_list:
                                fragment = new ShoppingListFragment();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment);
                        transaction.commit();
                        return true;
                    }
                }
        );
    }
}
