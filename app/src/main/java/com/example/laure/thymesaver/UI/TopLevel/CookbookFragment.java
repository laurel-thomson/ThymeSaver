package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.Adapters.RecipeAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.IngredientViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeViewModel;

import java.util.List;

public class CookbookFragment extends Fragment {
    private RecipeViewModel mRecipeViewModel;
    private IngredientViewModel mIngredientViewModel;
    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_cookbook, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mIngredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel.class);
        mRecipeViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                //update the cached copy of recipes in the adapter
                mAdapter.setRecipes(recipes);
            }
        });

        mRecyclerView = view.findViewById(R.id.recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecipeAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        Ingredient i = new Ingredient();
        i.setName("chips");
        Recipe r = new Recipe();
        r.setName("chips and salsa");
        r.addIngredient("chips",1);
        mRecipeViewModel.addRecipe(r);
        mIngredientViewModel.addIngredient(i);
    }
}
