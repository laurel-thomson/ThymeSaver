package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters.AddRecipesAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

import java.util.List;

public class AddPlannedMealsActivity extends AppCompatActivity {
    private AddRecipesAdapter mAdapter;
    private CookBookViewModel mCookBookViewModel;
    private PantryViewModel mPantryViewModel;
    private String mScheduledDay;
    public static String SCHEDULED_DAY = "Scheduled Day";
    private TextView mEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        final ProgressBar progressBar = findViewById(R.id.recycler_view_progress);
        mEmptyMessage = findViewById(R.id.empty_message);

        mScheduledDay = getIntent().getStringExtra(SCHEDULED_DAY);

        setUpActionBar();

        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        RecyclerView rv = findViewById(R.id.recycler_view);
        mAdapter = new AddRecipesAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == mAdapter.HEADER_TYPE) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        rv.setLayoutManager(layoutManager);

        mCookBookViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setTotalRecipes(recipes);
                progressBar.setVisibility(View.GONE);
            }
        });

        mPantryViewModel.getAllIngredients(new ValueCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> ingredients) {
                mAdapter.setTotalIngredients(ingredients);
            }

            @Override
            public void onError(String error) {

            }
        });

        rv.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_scheduled_meals);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
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

    @Override
    public void onBackPressed() {
        addMealPlans();
        super.onBackPressed();
    }

    private void addMealPlans() {
        List<Recipe> plannedRecipes = mAdapter.getPlannedRecipes();
        mCookBookViewModel.addRecipesToMealPlan(plannedRecipes, mScheduledDay);
    }
}