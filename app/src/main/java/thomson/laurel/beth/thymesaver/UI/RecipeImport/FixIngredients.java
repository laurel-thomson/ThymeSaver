package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;
import java.util.logging.Logger;

import thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters.FixIngredientsAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
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

        mPantryViewModel.getAllIngredients(new ValueCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
                ProgressBar progress = findViewById(R.id.progress_bar);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Imported Ingredients");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void onDoneClicked() {
        List<Ingredient> fixedIngredients = mAdapter.getFixedIngredients();
        IngredientAddedCB cb = new IngredientAddedCB(fixedIngredients.size());
        for (Ingredient ingredient : fixedIngredients) {
            mPantryViewModel.addImportedIngredient(ingredient, cb);
        }
    }


    private void launchRecipeDetailActivity() {
        Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, mRecipe.getName());
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    class IngredientAddedCB implements Callback {
        private int ingredientsAdded = 0;
        private int totalIngredients;

        IngredientAddedCB(int totalIngredients) {
            this.totalIngredients = totalIngredients;
        }

        @Override
        public void onSuccess() {
            ingredientsAdded++;
            if (ingredientsAdded == totalIngredients) {
               addRecipe();
            }
        }

        private void addRecipe() {
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

        @Override
        public void onError(String err) {

        }
    }
}
