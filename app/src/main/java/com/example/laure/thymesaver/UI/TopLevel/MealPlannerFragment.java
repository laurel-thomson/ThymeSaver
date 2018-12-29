package com.example.laure.thymesaver.UI.TopLevel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddPlannedMealsActivity;

public class MealPlannerFragment extends TopLevelFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_meal_planner, viewGroup, false);
    }

    @Override
    void launchAddItemActivity() {
        Intent intent = new Intent(getActivity(), AddPlannedMealsActivity.class);
        startActivity(intent);
    }
}
