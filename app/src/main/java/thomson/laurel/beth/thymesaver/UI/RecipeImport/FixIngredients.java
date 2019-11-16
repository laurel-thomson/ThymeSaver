package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters.FixIngredientsAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

public class FixIngredients extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FixIngredientsAdapter mAdapter;
    private PantryViewModel mPantryViewModel;
    private CookBookViewModel mCookBookViewModel;
    private Button mDoneButton;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_ingredients);
        setUpActionBar();

        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);

        mAdapter = new FixIngredientsAdapter(this);
        mRecyclerView = findViewById(R.id.fix_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAdapterData();

        mRecyclerView.setAdapter(mAdapter);

        mDoneButton = findViewById(R.id.button_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDoneClicked();
            }
        });

        closeSoftKeyboardOnFocusLost();
    }

    private void closeSoftKeyboardOnFocusLost() {
        findViewById(R.id.fix_ingredients_recycler_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void setAdapterData() {
        mRecipe = ImportedRecipe.getInstance().getRecipe();
        mAdapter.setRecipe(mRecipe);
        LifecycleOwner owner = this;

        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
                mPantryViewModel.getAllIngredients().removeObservers(owner);
            }
        });
    }


    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Imported Ingredients");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void onDoneClicked() {
        for (Ingredient ingredient : mAdapter.getFixedIngredients()) {
            mPantryViewModel.addIngredient(ingredient);
        }

        mRecipe.setRecipeIngredients(mAdapter.getRecipeIngredients());
        mCookBookViewModel.addRecipe(mRecipe, new Callback() {
            @Override
            public void onSuccess() {
                launchRecipeDetailActivity();
            }

            @Override
            public void onError(String err) {
                //TODO: show error - unable to import recipe
            }
        });
    }


    private void launchRecipeDetailActivity() {
        Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, mRecipe.getName());
        finish();
        startActivity(intent);
    }
}
