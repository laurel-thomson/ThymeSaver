package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.IngredientAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddNewIngredientActivity;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

import java.util.List;

public class PantryFragment extends AddableFragment implements IngredientAdapter.IngredientQuantityChangedListener {
    private PantryViewModel mIngredientViewModel;
    private IngredientAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_pantry, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.pantry_progress);
        mIngredientViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mAdapter = new IngredientAdapter(getActivity(),this);
        mIngredientViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
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
        mIngredientViewModel.updateIngredientPantryQuantity(ingredient, quantity);
    }

    @Override
    void launchAddItemActivity() {
        Intent intent = new Intent(getActivity(), AddNewIngredientActivity.class);
        startActivity(intent);
    }
}
