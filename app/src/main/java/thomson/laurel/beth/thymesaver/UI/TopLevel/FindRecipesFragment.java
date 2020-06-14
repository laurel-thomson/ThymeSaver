package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
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
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;

public class FindRecipesFragment extends Fragment {
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
}
