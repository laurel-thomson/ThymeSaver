package com.example.laure.thymesaver.UI.TopLevel;

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

import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.MealPlannerAdapter;
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.SectionedAdapter;
import com.example.laure.thymesaver.R;

import java.util.Calendar;


public class MealPlannerFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private MealPlannerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_meal_planner, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.meal_planner_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));

        String[] meals = {"Soup", "Pancakes", "Rice and Beans", "Spaghetti","Bananas","Soy milk","squash"};

        mAdapter = new MealPlannerAdapter(getActivity(),meals);


        SectionedAdapter.DaySection[] sections = {
                new SectionedAdapter.DaySection(0,"Sunday", Calendar.SUNDAY),
                new SectionedAdapter.DaySection(0,"Monday", Calendar.MONDAY),
                new SectionedAdapter.DaySection(1,"Tuesday", Calendar.TUESDAY),
                new SectionedAdapter.DaySection(1,"Wednesday", Calendar.WEDNESDAY),
                new SectionedAdapter.DaySection(3,"Thursday", Calendar.THURSDAY),
                new SectionedAdapter.DaySection(4,"Friday", Calendar.FRIDAY),
                new SectionedAdapter.DaySection(5,"Saturday", Calendar.SATURDAY)
        };
        SectionedAdapter mSectionedAdapter = new
                SectionedAdapter(getActivity(),R.layout.section,R.id.section_text,mAdapter);

        mSectionedAdapter.setSections(sections);

        mRecyclerView.setAdapter(mSectionedAdapter);
    }
}
