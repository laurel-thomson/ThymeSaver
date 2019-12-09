package thomson.laurel.beth.thymesaver.UI.TopLevel;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters.PantryAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.AddIngredients.AddIngredientFragment;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

import java.util.List;

public class PantryFragment extends AddButtonFragment implements PantryAdapter.IngredientListener {
    private PantryViewModel mViewModel;
    private PantryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mAdapter = new PantryAdapter(getActivity(),this);
        mEmptyMessage = view.findViewById(R.id.empty_message);

        setObserver();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setObserver() {
        if (mViewModel.getAllIngredients() == null) {
            //force the main activity to close and restart
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().finish();
            startActivity(intent);
            return;
        }

        mViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                if (ingredients.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setIngredients(ingredients);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onIngredientQuantityChanged(Ingredient ingredient, int quantity) {
        mViewModel.updateIngredientPantryQuantity(ingredient, quantity);
    }

    @Override
    public void onDeleteClicked(final Ingredient ingredient) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete " + ingredient.getName() + " ?")
                .setMessage("Are you sure you want to delete this ingredient? This will remove" +
                        " this ingredient from all recipes and the shopping list.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.deleteIngredient(ingredient);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_pantry, menu);
        super.onCreateOptionsMenu(menu, inflater);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public void onIngredientClicked(Ingredient ing) {
        Bundle bundle = new Bundle();
        bundle.putString(AddIngredientFragment.INGREDIENT_NAME, ing.getName());
        bundle.putString(AddIngredientFragment.INGREDIENT_CATEGORY, ing.getCategory());
        bundle.putBoolean(AddIngredientFragment.IS_BULK, ing.isBulk());

        AddIngredientFragment fragment = new AddIngredientFragment();
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onFABClicked() {
        AddIngredientFragment fragment = new AddIngredientFragment();
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }
}
