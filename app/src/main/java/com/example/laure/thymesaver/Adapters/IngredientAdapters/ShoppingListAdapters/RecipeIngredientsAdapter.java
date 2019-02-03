package com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters.MeasuredIngredientAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class RecipeIngredientsAdapter extends MeasuredIngredientAdapter {

    private SparseBooleanArray mStepCheckStates = new SparseBooleanArray();

    public RecipeIngredientsAdapter(
            Context context,
            HashMap<Ingredient, Integer> ingredients,
            MeasuredIngredientListener quantityChangedListener) {
        super(context, ingredients, quantityChangedListener);
    }

    public RecipeIngredientsAdapter(Context context, MeasuredIngredientListener quantityChangedListener ) {
        super(context, quantityChangedListener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean checked = compoundButton.isChecked();
                if (checked) {
                    holder.mNameTV.setPaintFlags(holder.mNameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.mNameTV.setTextColor(Color.GRAY);
                }
                else {
                    holder.mNameTV.setPaintFlags(0);
                    holder.mNameTV.setTextColor(Color.BLACK);
                }
                mStepCheckStates.put(position, checked);
            }
        });
        if (!mStepCheckStates.get(position, false)) {
            holder.mCheckBox.setChecked(false);}
        else {
            holder.mCheckBox.setChecked(true);
        }
    }

    public boolean[] getCheckStates() {
        boolean[] arr = new boolean[mIngredients.size()];
        for (int i = 0; i < mIngredients.size(); i++) {
            arr[i] = mStepCheckStates.get(i);
        }
        return arr;
    }

    public void restoreCheckStates(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            mStepCheckStates.put(i, arr[i]);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
}
