package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters.FixIngredientsAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.RecipeDetailViewModel;

public class FixIngredients extends AppCompatActivity implements FixIngredientsAdapter.MyListener {
    public static String RECIPE = "recipe";
    private RecyclerView mRecyclerView;
    private FixIngredientsAdapter mAdapter;
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private PantryViewModel mPantryViewModel;
    private List<Ingredient> mIngredients;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_ingredients);
        setUpActionBar();

        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);

        String recipeName = getIntent().getStringExtra(RECIPE);
        mRecipeDetailViewModel.setCurrentRecipe(recipeName);

        mAdapter = new FixIngredientsAdapter(this, this);
        mRecyclerView = findViewById(R.id.fix_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setObservers();

        mRecyclerView.setAdapter(mAdapter);
    }

    private void setObservers() {
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mIngredients = ingredients;
                mAdapter.setIngredients(mIngredients);
            }
        });

        mRecipeDetailViewModel.getCurrentRecipe(new ValueCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                mRecipe = recipe;
                mAdapter.setRecipe(mRecipe);
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
}
