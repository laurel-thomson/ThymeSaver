package com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients;

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
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.RecipeIngredientsAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddIngredientFragment;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeDetailFragment;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;

public class RecipeIngredientsFragment extends RecipeDetailFragment
        implements AddRecipeIngredientListener{

    private RecipeDetailViewModel mViewModel;
    private RecipeIngredientsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyMessage;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new RecipeIngredientsAdapter(getActivity(),
                this);
        mEmptyMessage = view.findViewById(R.id.empty_message);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        mViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) return;
                observeRecipeIngredients();
            }
        });

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        if (savedInstanceState != null) {
            boolean[] arr = (boolean[]) savedInstanceState.get("check_states");
            mAdapter.restoreCheckStates(arr);
        }
    }

    private void observeRecipeIngredients() {
        mViewModel.getRecipeIngredients().observe(this, new Observer<HashMap<Ingredient, RecipeQuantity>>() {
            @Override
            public void onChanged(@Nullable HashMap<Ingredient, RecipeQuantity> ingredients) {
                if (ingredients == null) return;
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBooleanArray("check_states", mAdapter.getCheckStates());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void addNewItem() {
        AddRecipeIngredientsFragment fragment = new AddRecipeIngredientsFragment();
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onIngredientClicked(Ingredient i, RecipeQuantity quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(AddRecipeIngredientsFragment.INGREDIENT_NAME, i.getName());
        bundle.putString(AddRecipeIngredientsFragment.INGREDIENT_UNIT, quantity.getUnit());
        bundle.putDouble(AddRecipeIngredientsFragment.INGREDIENT_QUANTITY, quantity.getRecipeQuantity());

        AddRecipeIngredientsFragment fragment = new AddRecipeIngredientsFragment();
        fragment.setArguments(bundle);
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onIngredientLongClicked(Ingredient ing) {
        Bundle bundle = new Bundle();
        bundle.putString(AddIngredientFragment.INGREDIENT_NAME, ing.getName());
        bundle.putString(AddIngredientFragment.INGREDIENT_CATEGORY, ing.getCategory());
        bundle.putBoolean(AddIngredientFragment.IS_BULK, ing.isBulk());

        AddIngredientFragment fragment = new AddIngredientFragment();
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
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
                        mViewModel.addRecipeIngredient(
                                i.getName(),
                                quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Recipe ingredient restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onIngredientAdded(Ingredient ingredient, RecipeQuantity quantity) {
        mViewModel.addRecipeIngredient(ingredient.getName(), quantity);
    }
}
