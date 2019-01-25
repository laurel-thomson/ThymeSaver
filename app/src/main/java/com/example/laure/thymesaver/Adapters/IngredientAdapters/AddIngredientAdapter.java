package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class AddIngredientAdapter extends MeasuredIngredientAdapter {

    public AddIngredientAdapter(Context context, MeasuredIngredientListener listener) {
        super(context, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String name = mFilteredIngredients.get(position);
        holder.mNameTV.setText(name);
        holder.mCheckBox.setChecked(mMeasuredIngredients.get(name) > 0);
        if (holder.mCheckBox.isChecked()) {
            holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(name)));
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
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new AddIngredientViewHolder(itemView);
    }

    public class AddIngredientViewHolder extends MyViewHolder {

        public AddIngredientViewHolder(View view) {
            super(view);
            mCheckBox.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String name = mFilteredIngredients.get(getAdapterPosition());
                    if (compoundButton.isChecked()) {
                        if (mMeasuredIngredients.get(name) == 0) {
                            mMeasuredIngredients.put(name, 1);
                            mQuantityTV.setText("1");
                        }
                        mQuantityTV.setVisibility(View.VISIBLE);
                        mIncrementer.setVisibility(View.VISIBLE);
                        mDecrementer.setVisibility(View.VISIBLE);
                    }
                    else {
                        mMeasuredIngredients.put(name,0);
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
        for (String i : mIngredients) {
            int recipeQuantity = mMeasuredIngredients.get(i);
            if (recipeQuantity > 0) {
                recipeIngredients.put(i, recipeQuantity);
            }
        }
        return recipeIngredients;
    }
}
