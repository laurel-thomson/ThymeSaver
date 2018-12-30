package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.SwipeAndDragHelper;
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.MealPlannerAdapter;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.MealPlannerViewModel;

import java.util.ArrayList;
import java.util.List;


public class MealPlannerFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MealPlannerAdapter mAdapter;
    private MealPlannerViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_meal_planner, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = ViewModelProviders.of(getActivity()).get(MealPlannerViewModel.class);
        mRecyclerView = view.findViewById(R.id.meal_planner_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MealPlannerAdapter();
        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mViewModel.getMealPlans().observe(this, new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(@Nullable List<MealPlan> mealPlans) {
                mAdapter.setMealPlans(mealPlans);
            }
        });
    }
}
