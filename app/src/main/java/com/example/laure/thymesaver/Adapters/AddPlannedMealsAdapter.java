package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddPlannedMealsAdapter extends RecyclerView.Adapter<AddPlannedMealsAdapter.MyViewHolder> {
    private List<Recipe> mTotalRecipes;
    private List<Recipe> mPlannedRecipes;
    private final LayoutInflater mInflater;

    public AddPlannedMealsAdapter(Context context,
                                  List<Recipe> totalRecipes,
                                  List<Recipe> plannedRecipes) {
        mInflater = LayoutInflater.from(context);
        mTotalRecipes = totalRecipes;
        mPlannedRecipes = plannedRecipes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) mInflater
                .inflate(R.layout.multiselect_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Recipe recipe = mTotalRecipes.get(position);
        holder.mNameTV.setText(recipe.getName());
        int plannedQuantity = getPlannedMealQuantity(recipe);
        holder.mCheckBox.setChecked(plannedQuantity > 0);
        if (holder.mCheckBox.isChecked()) {
            holder.mQuantityTV.setText(Integer.toString(plannedQuantity));
        }
        else {
            holder.mQuantityTV.setText("0");
            holder.mQuantityTV.setVisibility(View.GONE);
            holder.mIncrementer.setVisibility(View.GONE);
            holder.mDecrementer.setVisibility(View.GONE);
        }
    }

    private int getPlannedMealQuantity(Recipe recipe) {
        int quantity = 0;
        for (Recipe r : mTotalRecipes) {
            if (r.getName().equals(recipe.getName())) {
                quantity++;
            }
        }
        return quantity;
    }

    @Override
    public int getItemCount() {
        if (mTotalRecipes == null) return 0;
        return mTotalRecipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox mCheckBox;
        public TextView mNameTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;

        public MyViewHolder(@NonNull View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Recipe recipe = mTotalRecipes.get(getAdapterPosition());
                    int plannedQuantity = getPlannedMealQuantity(recipe);
                    if (compoundButton.isChecked()) {
                        if (plannedQuantity == 0) {
                            mPlannedRecipes.add(recipe);
                            mQuantityTV.setText("1");
                        }
                        mQuantityTV.setVisibility(View.VISIBLE);
                        mIncrementer.setVisibility(View.VISIBLE);
                        mDecrementer.setVisibility(View.VISIBLE);
                    }
                    else {
                        removeAllPlannedRecipes(recipe);
                        mQuantityTV.setVisibility(View.GONE);
                        mIncrementer.setVisibility(View.GONE);
                        mDecrementer.setVisibility(View.GONE);
                    }
                }
            });
        }

        private void removeAllPlannedRecipes(Recipe recipe) {
            Collection<Recipe> collection = new ArrayList<>();
            collection.add(recipe);
            mPlannedRecipes.removeAll(collection);
        }
    }
}
