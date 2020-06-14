package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.List;

import thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters.FindRecipesAdapter;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;

public class FindRecipesFragment extends AddButtonFragment {
    private CookBookViewModel mViewModel;
    private FindRecipesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Button mSearchButton;
    private EditText mQueryEditText;

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
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mAdapter = new FindRecipesAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

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
        new FindRecipeClient().getRecipes(query, new ValueCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> value) {
                mAdapter.setRecipes(value);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void onRecipeSelected(Recipe recipe, View recipeImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                recipe.getName());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View)recipeImage, "recipe_image");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onFABClicked() {

    }
}
