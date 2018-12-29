package com.example.laure.thymesaver.UI.RecipeDetail;

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
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeIngredientsFragment extends RecipeDetailFragment
        implements MeasuredIngredientAdapter.IngredientQuantityChangedListener {

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

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        mAdapter = new ChecklistIngredientAdapter(getActivity(),
                mViewModel.getRecipeIngredients(),
                this);

        mRecyclerView = view.findViewById(R.id.recipe_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onResume() {
        super.onResume();
        //when the fragment is resumed after the AddRecipeIngredientsActivity is completed, we
        //want to refresh the list of recipe ingredients
        mAdapter.setIngredients(mViewModel.getRecipeIngredients());
    }

    public void onIngredientQuantityChanged(Ingredient ingredient, int quantity) {
        mViewModel.updateRecipeIngredientQuantity(ingredient, quantity);
    }

    @Override
    void launchAddItemActivity() {
        Intent intent = new Intent(getActivity(), AddRecipeIngredientsActivity.class);
        intent.putExtra(AddRecipeIngredientsActivity.RECIPE_NAME, mViewModel.getCurrentRecipe().getName());
        startActivity(intent);
    }
}
