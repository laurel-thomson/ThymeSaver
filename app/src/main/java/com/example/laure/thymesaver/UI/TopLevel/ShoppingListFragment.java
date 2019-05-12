package com.example.laure.thymesaver.UI.TopLevel;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddShoppingListItemsActivity;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;

public class ShoppingListFragment extends AddButtonFragment implements ShoppingListAdapter.ShoppingListListener {
    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_shopping_list, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.shopping_list_progress);
        mRecyclerView = view.findViewById(R.id.shopping_recycler_view);
        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mAdapter = new ShoppingListAdapter(getActivity(), this);

        setObserver();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setObserver() {
        if (mViewModel.getShoppingList() == null) {
            //force the main activity to close and restart
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().finish();
            startActivity(intent);
            return;
        }

        mViewModel.getShoppingList().observe(this, new Observer<HashMap<Ingredient, Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<Ingredient, Integer> ingredientIntegerHashMap) {
                mAdapter.setIngredients(ingredientIntegerHashMap);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    void launchAddItemActivity() {
        Intent intent = new Intent(getActivity(), AddShoppingListItemsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, int quantity) {
        mViewModel.addShoppingModification(i.getName(), ModType.CHANGE, quantity);
    }

    @Override
    public void onIngredientCheckedOff(Ingredient i, int quantity) {
        if (mViewModel.ingredientExists(i)) {
            addQuantityToPantry(i, quantity);
        }
        else {
            askAddIngredientToPantry(i, quantity);
        }
    }

    private void askAddIngredientToPantry(final Ingredient ing, final int quantity) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Add Ingredient to Pantry?")
                .setMessage("Would you like to store this ingredient in the pantry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addQuantityToPantry(ing, quantity);
                    }
                })
                .setNegativeButton("NO", null)
                .create();
        dialog.show();
    }

    private void addQuantityToPantry(Ingredient i, int quantity) {
        mViewModel.addQuantityToPantry(i, quantity);
        mViewModel.deleteModifier(i.getName());
    }

    @Override
    public void onDeleteClicked(Ingredient i, int quantity) {
        mViewModel.deleteShoppingListItem(i, quantity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_renew:
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Refresh Shopping List?")
                        .setMessage("Refreshing the shopping list will delete all " +
                                "manual modifications.")
                        .setPositiveButton("REFRESH", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mViewModel.refreshShoppingList();
                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;
    }
}
