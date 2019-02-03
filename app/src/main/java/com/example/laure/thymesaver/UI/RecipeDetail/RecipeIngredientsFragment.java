package com.example.laure.thymesaver.UI.RecipeDetail;

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

import com.example.laure.thymesaver.Adapters.IngredientAdapters.RecipeIngredientsAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeIngredientsFragment extends RecipeDetailFragment
        implements RecipeIngredientsAdapter.Listener{

    private RecipeDetailViewModel mViewModel;
    private RecipeIngredientsAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_ingredients, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new RecipeIngredientsAdapter(getActivity(),
                this);

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        mViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) {
                    return;
                }
                //todo : update recipe ingredients
            }
        });

        mRecyclerView = view.findViewById(R.id.recipe_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        if (savedInstanceState != null) {
            boolean[] arr = (boolean[]) savedInstanceState.get("check_states");
            mAdapter.restoreCheckStates(arr);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBooleanArray("check_states", mAdapter.getCheckStates());
        super.onSaveInstanceState(outState);
    }

    @Override
    void addNewItem() {
        AddRecipeIngredientsFragment fragment = new AddRecipeIngredientsFragment();
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, RecipeQuantity quantity) {
        mViewModel.updateRecipeIngredientQuantity(i.getName(), quantity);
    }

    @Override
    public void onDeleteClicked(final Ingredient i, final RecipeQuantity quantity) {
        mViewModel.deleteRecipeIngredient(i.getName());
        Snackbar snackbar = Snackbar
                .make(getView(), i.getName() +
                        " removed from recipe.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.updateRecipeIngredientQuantity(
                                i.getName(),
                                quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Recipe ingredient restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }
}
