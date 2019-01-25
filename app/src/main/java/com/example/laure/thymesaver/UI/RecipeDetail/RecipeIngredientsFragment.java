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

import com.example.laure.thymesaver.Adapters.IngredientAdapters.ChecklistIngredientAdapter;
import com.example.laure.thymesaver.Adapters.IngredientAdapters.MeasuredIngredientAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeIngredientsFragment extends RecipeDetailFragment
        implements MeasuredIngredientAdapter.MeasuredIngredientListener {

    private RecipeDetailViewModel mViewModel;
    private ChecklistIngredientAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_ingredients, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ChecklistIngredientAdapter(getActivity(),
                this);

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        mViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) {
                    return;
                }
                mAdapter.setIngredients(recipe.getRecipeIngredients());
            }
        });

        mRecyclerView = view.findViewById(R.id.recipe_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    public void onIngredientQuantityChanged(String ingredientName, int quantity) {
        mViewModel.updateRecipeIngredientQuantity(ingredientName, quantity);
    }

    @Override
    public void onIngredientCheckedOff(String ingredientName, int quantity) {
        //do nothing
    }

    @Override
    public void onDeleteClicked(final String ingredientName, final int quantity) {
        mViewModel.deleteRecipeIngredient(ingredientName);
        Snackbar snackbar = Snackbar
                .make(getView(), ingredientName +
                        " removed from recipe.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.updateRecipeIngredientQuantity(ingredientName, quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Recipe ingredient restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    void addNewItem() {
        Intent intent = new Intent(getActivity(), AddRecipeIngredientsActivity.class);
        intent.putExtra(AddRecipeIngredientsActivity.RECIPE_NAME, mViewModel.getCurrentRecipeName());
        startActivity(intent);
    }
}
