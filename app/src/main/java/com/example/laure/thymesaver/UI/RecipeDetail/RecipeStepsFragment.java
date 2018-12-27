package com.example.laure.thymesaver.UI.RecipeDetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.laure.thymesaver.Adapters.RecipeStepAdapter;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeStepsFragment extends RecipeDetailFragment
        implements AddStepFragment.StepAddedListener, RecipeStepAdapter.RecipeStepAdapterListener {
    private RecipeStepAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<String> mSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_recipe_steps, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.steps_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //empty steps list for now
        mSteps = new ArrayList<>();
        mAdapter = new RecipeStepAdapter(getActivity(), mSteps, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    void launchAddItemActivity() {
        AddStepFragment fragment = new AddStepFragment();
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onStepAdded(String step) {
        mSteps.add(step);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStepSelected(String step) {
        //todo: do something when step selected
    }
}
