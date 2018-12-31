package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.DragHelper;
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.MealPlannerAdapter;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import com.example.laure.thymesaver.ViewModels.MealPlannerViewModel;

import java.util.List;


public class MealPlannerFragment extends Fragment implements MealPlannerAdapter.MealPlanListener {
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

        final ProgressBar progressBar = view.findViewById(R.id.meal_planner_progress);

        mViewModel = ViewModelProviders.of(getActivity()).get(MealPlannerViewModel.class);
        mRecyclerView = view.findViewById(R.id.meal_planner_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MealPlannerAdapter(this);
        DragHelper swipeAndDragHelper = new DragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mViewModel.getMealPlans().observe(this, new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(@Nullable List<MealPlan> mealPlans) {
                mAdapter.setMealPlans(mealPlans);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onMealScheduleChanged(MealPlan mealPlan) {
        mViewModel.updateMealPlan(mealPlan);
    }

    @Override
    public void onMealClicked(MealPlan mealPlan) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                mealPlan.getRecipeName());
        startActivity(intent);
    }

    @Override
    public void onMealChecked(MealPlan mealPlan, boolean checked) {
        String message;
        if (checked) {
            message = "Ingredients from " + mealPlan.getRecipeName() + " removed from pantry.";
        }
        else {
            message = "Ingredients from " + mealPlan.getRecipeName() + " added back to pantry.";
        }
        mealPlan.setCooked(checked);
        mViewModel.updateMealPlan(mealPlan);
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
