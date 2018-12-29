package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class AddIngredientAdapter extends MeasuredIngredientAdapter {

    public AddIngredientAdapter(Context context, IngredientQuantityChangedListener listener) {
        super(context, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Ingredient ingredient = mFilteredIngredients.get(position);
        holder.mNameTV.setText(ingredient.getName());
        holder.mCheckBox.setChecked(mMeasuredIngredients.get(ingredient) > 0);
        if (holder.mCheckBox.isChecked()) {
            holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(ingredient)));
        }
        else {
            holder.mQuantityTV.setText("0");
            holder.mQuantityTV.setVisibility(View.GONE);
            holder.mIncrementer.setVisibility(View.GONE);
            holder.mDecrementer.setVisibility(View.GONE);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.multiselect_list_item, parent, false);
        return new AddIngredientViewHolder(itemView);
    }

    public class AddIngredientViewHolder extends MyViewHolder {

        public AddIngredientViewHolder(View view) {
            super(view);
            mCheckBox.setVisibility(View.VISIBLE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (compoundButton.isChecked()) {
                        if (mMeasuredIngredients.get(i) == 0) {
                            mMeasuredIngredients.put(i, 1);
                            mQuantityTV.setText("1");
                        }
                        mQuantityTV.setVisibility(View.VISIBLE);
                        mIncrementer.setVisibility(View.VISIBLE);
                        mDecrementer.setVisibility(View.VISIBLE);
                    }
                    else {
                        mMeasuredIngredients.put(i,0);
                        mQuantityTV.setVisibility(View.GONE);
                        mIncrementer.setVisibility(View.GONE);
                        mDecrementer.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public HashMap<String, Integer> getRecipeIngredients() {
        HashMap<String, Integer> recipeIngredients = new HashMap<>();
        for (Ingredient i : mIngredients) {
            int recipeQuantity = mMeasuredIngredients.get(i);
            if (recipeQuantity > 0) {
                recipeIngredients.put(i.getName(), recipeQuantity);
            }
        }
        return recipeIngredients;
    }
}
