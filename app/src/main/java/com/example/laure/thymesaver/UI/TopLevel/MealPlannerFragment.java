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
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.MyAdapter;
import com.example.laure.thymesaver.R;


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


        MyAdapter.Section[] sections = {
                new MyAdapter.Section(0,"Sunday"),
                new MyAdapter.Section(1,"Monday"),
                new MyAdapter.Section(2,"Tuesday"),
                new MyAdapter.Section(2,"Wednesday"),
                new MyAdapter.Section(4,"Thursday"),
                new MyAdapter.Section(5,"Friday"),
                new MyAdapter.Section(7,"Saturday")
        };
        MyAdapter mSectionedAdapter = new
                MyAdapter(getActivity(),R.layout.section,R.id.section_text,mAdapter);

        mSectionedAdapter.setSections(sections);

        mRecyclerView.setAdapter(mSectionedAdapter);
    }
}
