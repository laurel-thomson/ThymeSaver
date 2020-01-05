package thomson.laurel.beth.thymesaver.UI.RecipeImport;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients.ImportClient;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

public class ImportActivity extends AppCompatActivity {
    private ActionBar mActionBar;
    private CookBookViewModel mCookBookViewModel;
    private PantryViewModel mPantryViewModel;
    private Button mSearchButton;
    private EditText mURLEditText;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        setUpActionBar();

        mProgress = findViewById(R.id.import_progress);
        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mURLEditText= findViewById(R.id.url_edittext);
        mSearchButton = findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setVisibility(View.VISIBLE);
                hideKeyboard();
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
        ImportedRecipe.getInstance().setRecipe(recipe);
        Intent intent = new Intent(getApplicationContext(), FixIngredients.class);
        finish();
        startActivity(intent);
    }

    private void onImportFail(String error) {
        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(this.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mProgress.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(findViewById(R.id.url_edittext), "Failed to import recipe", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };
        mainHandler.post(myRunnable);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
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
