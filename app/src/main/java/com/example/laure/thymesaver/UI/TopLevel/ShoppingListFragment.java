package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.RecipeIngredientsAdapter;
import com.example.laure.thymesaver.Adapters.IngredientAdapters.MeasuredIngredientAdapter;
import com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapter;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;

public class ShoppingListFragment extends AddButtonFragment implements MeasuredIngredientAdapter.MeasuredIngredientListener {
    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_shopping_list, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.shopping_list_progress);

        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mAdapter = new ShoppingListAdapter(getActivity(), this);
        mViewModel.getShoppingList().observe(this, new Observer<HashMap<String,Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Integer> shoppingList) {
                mAdapter.setIngredients(shoppingList);
                progressBar.setVisibility(View.GONE);
            }
        });

        mRecyclerView = view.findViewById(R.id.shopping_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    void launchAddItemActivity() {
        Toast.makeText(getActivity(), "Add Shopping List not implemented", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIngredientQuantityChanged(String ingredientName, int quantity) {
        mViewModel.addShoppingModification(ingredientName, quantity);
    }

    @Override
    public void onIngredientCheckedOff(final String ingredientName, final int quantity) {
        mViewModel.addQuantityToPantry(ingredientName, quantity);
        Snackbar snackbar = Snackbar
                .make(getView(), ingredientName +
                        " added back to pantry.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.removeQuantityFromPantry(ingredientName, quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Shopping list item restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onDeleteClicked(String ingredientName, int quantity) {
        //todo: remove this item from the list
    }
}
