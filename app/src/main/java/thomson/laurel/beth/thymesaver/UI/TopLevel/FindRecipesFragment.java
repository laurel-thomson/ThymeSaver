package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters.FindRecipesAdapter;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.Step;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.UI.RecipeImport.FixIngredients;
import thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportClients.ImportStepsClient;
import thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportedRecipe;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;

public class FindRecipesFragment extends Fragment implements FindRecipesAdapter.FindRecipesListener {
    private CookBookViewModel mViewModel;
    private FindRecipesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mSearchButton;
    private EditText mQueryEditText;
    private TextView mEmptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_find_recipes, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mSearchButton = view.findViewById(R.id.search_button);
        mQueryEditText = view.findViewById(R.id.query_edittext);
        mEmptyMessage = view.findViewById(R.id.empty_message);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mAdapter = new FindRecipesAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                mAdapter.setCookbook(recipes);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = mQueryEditText.getText().toString();
                if (!query.equals("")) {
                    onSearchClicked(query);
                }
            }
        });
    }

    private void onSearchClicked(String query) {
        mProgressBar.setVisibility(View.VISIBLE);
        mEmptyMessage.setVisibility(View.GONE);
        mSearchButton.setEnabled(false);
        new FindRecipeClient().getRecipes(query, new ValueCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setRecipes(recipes);
                        mProgressBar.setVisibility(View.GONE);
                        if (recipes == null || recipes.size() == 0) {
                            mEmptyMessage.setVisibility(View.VISIBLE);
                        }
                        mSearchButton.setEnabled(true);
                        hideKeyboard();
                    }
                });
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                        mEmptyMessage.setVisibility(View.VISIBLE);
                        mSearchButton.setEnabled(true);
                        hideKeyboard();
                    }
                });
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onFavoriteClicked(Recipe recipe) {
        mProgressBar.setVisibility(View.VISIBLE);
        new ImportStepsClient().importSteps(recipe.getSourceURL(), new ValueCallback<List<Step>>() {
            @Override
            public void onSuccess(List<Step> steps) {
                recipe.setSteps(steps);
                ImportedRecipe.getInstance().setRecipe(recipe);
                Intent intent = new Intent(getContext(), FixIngredients.class);
                startActivity(intent);
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String error) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void launchRecipeClicked(Recipe recipe, View recipeImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                recipe.getName());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View)recipeImage, "recipe_image");
        startActivity(intent, options.toBundle());
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUnfavoriteClicked(Recipe recipe) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Remove " + recipe.getName() + " ?")
                .setMessage("Are you sure you want to remove this recipe from the cookbook? This cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.deleteRecipe(recipe);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
