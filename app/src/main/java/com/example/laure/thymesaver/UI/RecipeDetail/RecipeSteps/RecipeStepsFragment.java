package com.example.laure.thymesaver.UI.RecipeDetail.RecipeSteps;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.Adapters.DragHelper;
import com.example.laure.thymesaver.Adapters.RecipeStepAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeDetailFragment;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.List;

public class RecipeStepsFragment extends RecipeDetailFragment
        implements RecipeStepListener {
    private RecipeStepAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<String> mSteps;
    private RecipeDetailViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_steps, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);

        mRecyclerView = view.findViewById(R.id.steps_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) {
                    return;
                }
                mSteps = recipe.getSteps();
                mAdapter.setSteps(recipe.getSteps());
            }
        });

        mAdapter = new RecipeStepAdapter(getActivity(), this);
        DragHelper swipeAndDragHelper = new DragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

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
    public void addNewItem() {
        AddStepFragment fragment = new AddStepFragment();
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onStepAdded(String step) {
        mSteps.add(step);
        mAdapter.notifyDataSetChanged();
        mViewModel.updateRecipe();
    }

    @Override
    public void onStepDeleted(final int position) {
        final String step = mSteps.get(position);
        mSteps.remove(position);
        mAdapter.notifyDataSetChanged();
        mViewModel.updateRecipe();
        Snackbar snackbar = Snackbar
                .make(getView(), "Step removed from recipe.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSteps.add(position, step);
                        mAdapter.notifyDataSetChanged();
                        mViewModel.updateRecipe();
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Recipe step restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onStepMoved(List<String> newList) {
        mViewModel.updateRecipe();
    }

    @Override
    public void onStepUpdated(String step, int position) {
        mSteps.set(position, step);
        mAdapter.notifyDataSetChanged();
        mViewModel.updateRecipe();
    }

    @Override
    public void onStepClicked(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(UpdateStepFragment.STEP_POSITION, position);
        bundle.putString(UpdateStepFragment.STEP_STRING, mSteps.get(position));
        UpdateStepFragment fragment = new UpdateStepFragment();
        fragment.setArguments(bundle);
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }
}