package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.Callback;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

public class ImportActivity extends AppCompatActivity {
    private ActionBar mActionBar;
    private CookBookViewModel mCookBookViewModel;
    private PantryViewModel mPantryViewModel;
    private Button mSearchButton;
    private EditText mURLEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        setUpActionBar();

        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mURLEditText= findViewById(R.id.url_edittext);
        mSearchButton = findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImportClient().importRecipe(mURLEditText.getText().toString(), new ValueCallback<Recipe>() {
                    @Override
                    public void onSuccess(Recipe recipe) {
                        onRecipeImported(recipe);
                    }

                    @Override
                    public void onError(String error) {
                        onImportFail(error);
                    }
                });
            }
        });
    }

    private void onRecipeImported(Recipe recipe) {
        addRecipeIngredients(recipe);
        mCookBookViewModel.addRecipe(recipe, new Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(getApplicationContext(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, recipe.getName());
                finish();
                startActivity(intent);
            }

            @Override
            public void onError(String err) {
                //TODO: show error - unable to import recipe
            }
        });
    }

    private void addRecipeIngredients(Recipe recipe) {
        for (String ingredientName : recipe.getRecipeIngredients().keySet()) {
            mPantryViewModel.addIngredient(new Ingredient(ingredientName, "Misc", true));
        }
    }

    private void onImportFail(String error) {

    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Import Recipe");
        mActionBar.setDisplayHomeAsUpEnabled(true);
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

}
