package thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeIngredients;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Adapters.AddRecipesAdapter;
import thomson.laurel.beth.thymesaver.Adapters.AddSubRecipesAdapter;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;

import java.util.List;

public class AddSubRecipesActivity extends AppCompatActivity {
    public static final String SUB_RECIPES = "Sub recipes";
    public static final String PARENT_RECIPE = "Parent recipe";
    private String mParentRecipeName;
    private AddSubRecipesAdapter mAdapter;
    private CookBookViewModel mCookBookViewModel;
    private TextView mEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);
        final ProgressBar progressBar = findViewById(R.id.recycler_view_progress);
        mEmptyMessage = findViewById(R.id.empty_message);
        setUpActionBar();
        mParentRecipeName = getIntent().getStringExtra(PARENT_RECIPE);

        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new AddSubRecipesAdapter(this);

        mCookBookViewModel.getAvailableSubRecipes(mParentRecipeName).observe(this, new Observer<List<Recipe>>() {
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

        rv.setAdapter(mAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_sub_recipes);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                addSubRecipes();
                return true;
        }
        return false;
    }

    private void addSubRecipes() {
        String[] subRecipes = mAdapter.getRecipesArray();
        Intent intent = new Intent();
        intent.putExtra(SUB_RECIPES, subRecipes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}