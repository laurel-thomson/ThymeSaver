package com.example.laure.thymesaver.UI.TopLevel;

import android.app.ActionBar;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddShoppingListItemFragment;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ShoppingListFragment extends AddButtonFragment
        implements ShoppingListAdapter.ShoppingListListener {

    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mAdapter = new ShoppingListAdapter(getActivity(), this);
        mEmptyMessage = view.findViewById(R.id.empty_message);

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
            public void onChanged(@Nullable HashMap<Ingredient, Integer> shoppingList) {
                if (shoppingList.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setIngredients(shoppingList);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    void launchAddItemActivity() {
        AddShoppingListItemFragment fragment = new AddShoppingListItemFragment();
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, int quantity) {
        mViewModel.addShoppingModification(i.getName(), ModType.CHANGE, quantity);
    }

    @Override
    public void onIngredientCheckedOff(Ingredient i, int quantity) {
        mViewModel.tryFindIngredient(i, new ValueCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient ingredient) {
                addQuantityToPantry(i, quantity);
            }

            @Override
            public void onError(String error) {
                askAddIngredientToPantry(i, quantity);
            }
        });
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
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteModifier(ing.getName());
                    }
                })
                .create();
        dialog.show();
    }

    private void addQuantityToPantry(Ingredient i, int quantity) {
        mViewModel.deleteModifier(i.getName());
        mViewModel.addQuantityToPantry(i, quantity);
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
