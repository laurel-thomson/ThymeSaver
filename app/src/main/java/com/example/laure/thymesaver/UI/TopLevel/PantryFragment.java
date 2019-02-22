package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.PantryAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddOrEditIngredientActivity;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

import java.util.List;

public class PantryFragment extends AddButtonFragment implements PantryAdapter.IngredientListener {
    private PantryViewModel mViewModel;
    private PantryAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_pantry, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.pantry_progress);

        mViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mAdapter = new PantryAdapter(getActivity(),this);
        mViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mAdapter.setIngredients(ingredients);
                progressBar.setVisibility(View.GONE);
            }
        });

        mRecyclerView = view.findViewById(R.id.pantry_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    public void onIngredientQuantityChanged(Ingredient ingredient, int quantity) {
        mViewModel.updateIngredientPantryQuantity(ingredient, quantity);
    }

    @Override
    public void onDeleteClicked(final Ingredient ingredient) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete " + ingredient.getName() + " ?")
                .setMessage("Are you sure you want to delete this ingredient? This will remove" +
                        " this ingredient from all recipes.")
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
    public void onIngredientClicked(Ingredient ingredient) {
        Intent intent = new Intent(getActivity(), AddOrEditIngredientActivity.class);
        intent.putExtra(AddOrEditIngredientActivity.INGREDIENT_NAME_KEY, ingredient.getName());
        startActivity(intent);
    }

    @Override
    void launchAddItemActivity() {
        Intent intent = new Intent(getActivity(), AddOrEditIngredientActivity.class);
        startActivity(intent);
    }
}
