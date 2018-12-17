package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.IngredientAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredientsActivity;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeIngredientsFragment extends Fragment  implements IngredientAdapter.IngredientAdapterListener{
    private RecipeDetailViewModel mViewModel;
    private IngredientAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_ingredients, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);

        mRecyclerView = view.findViewById(R.id.recipe_ingredients_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new IngredientAdapter(getActivity(),
                mViewModel.getCurrentRecipeIngredients(),
                this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        FloatingActionButton button = view.findViewById(R.id.add_ingredient_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddIngredientsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onIngredientSelected(Ingredient ingredient) {
        Toast.makeText(
                getContext(),
                ingredient.getName(),
                Toast.LENGTH_SHORT)
                .show();
    }
}
