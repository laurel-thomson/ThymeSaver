package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class MultiselectIngredientAdapter extends IngredientAdapter {
    private HashMap<Ingredient, Integer> mSelectedMeasuredIngredients;

    public MultiselectIngredientAdapter(Context context, IngredientAdapterListener listener) {
        super(context, listener);
    }

    public void setSelectedMeasuredIngredients(HashMap<Ingredient,Integer> selectedMeasuredIngredients) {
        mSelectedMeasuredIngredients = selectedMeasuredIngredients;
    }

    public HashMap<Ingredient,Integer> getSelectedMeasuredIngredients() {
        return mSelectedMeasuredIngredients;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Ingredient ingredient = mFilteredIngredients.get(position);
        holder.mNameTV.setText(ingredient.getName());
        holder.mCheckBox.setChecked(mSelectedMeasuredIngredients.containsKey(ingredient));
        if (holder.mCheckBox.isChecked()) {
            holder.mQuantityTV.setText(Integer.toString(mSelectedMeasuredIngredients.get(ingredient)));
        }
        else {
            holder.mIncrementer.setVisibility(View.GONE);
            holder.mDecrementer.setVisibility(View.GONE);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new MultiselectViewHolder(itemView);
    }

    public class MultiselectViewHolder extends MyViewHolder {

        public MultiselectViewHolder(View view) {
            super(view);
            mCheckBox.setVisibility(View.VISIBLE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (compoundButton.isChecked()) {
                        mSelectedMeasuredIngredients.put(i, i.getQuantity());
                        mQuantityTV.setVisibility(View.VISIBLE);
                        mIncrementer.setVisibility(View.VISIBLE);
                        mDecrementer.setVisibility(View.VISIBLE);
                    }
                    else {
                        mSelectedMeasuredIngredients.remove(i);
                        mQuantityTV.setVisibility(View.GONE);
                        mIncrementer.setVisibility(View.GONE);
                        mDecrementer.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
