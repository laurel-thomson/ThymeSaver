package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters.ShoppingListAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddShoppingListItemsActivity;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;

public class ShoppingListFragment extends AddButtonFragment implements ShoppingListAdapter.ShoppingListListener {
    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_shopping_list, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.shopping_list_progress);

        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mAdapter = new ShoppingListAdapter(getActivity(), this);
        mViewModel.getShoppingList().observe(this, new Observer<HashMap<Ingredient, Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<Ingredient, Integer> ingredientIntegerHashMap) {
                mAdapter.setIngredients(ingredientIntegerHashMap);
                progressBar.setVisibility(view.GONE);
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
        Intent intent = new Intent(getActivity(), AddShoppingListItemsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, int quantity) {
        mViewModel.addShoppingModification(i.getName(), quantity);
    }

    @Override
    public void onIngredientCheckedOff(final Ingredient i, final int quantity) {
        final int oldQuantity = i.getQuantity();
        mViewModel.addQuantityToPantry(i, quantity);
        mViewModel.deleteModifier(i.getName());
        Snackbar snackbar = Snackbar
                .make(getView(), i.getName() +
                        " added back to pantry.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.resetPantryQuantity(i, oldQuantity);
                        mViewModel.addShoppingModification(i.getName(), quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Shopping list item restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onDeleteClicked(Ingredient i, int quantity) {
        //todo: remove this item from the list
    }
}
