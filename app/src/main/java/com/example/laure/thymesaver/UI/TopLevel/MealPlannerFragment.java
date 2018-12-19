package com.example.laure.thymesaver.UI.TopLevel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.laure.thymesaver.R;

public class MealPlannerFragment extends TopLevelFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_meal_planner, viewGroup, false);
    }

    @Override
    void launchAddItemActivity() {
        Toast.makeText(getActivity(), "Add Recipe to Meal Plan not implemented", Toast.LENGTH_SHORT).show();
    }
}
