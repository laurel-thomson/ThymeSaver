package com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class AddShoppingItemsAdapter extends MeasuredIngredientAdapter {

    public AddShoppingItemsAdapter(Context context, MeasuredIngredientListener listener) {
        super(context, listener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Ingredient i = mFilteredIngredients.get(position);
        holder.mNameTV.setText(i.getName());
        holder.mCheckBox.setChecked(mMeasuredIngredients.get(i) > 0);
        if (holder.mCheckBox.isChecked()) {
            holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(i)));
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
            mDeleteButton.setVisibility(View.GONE);
            mUnitTV.setVisibility(View.GONE);

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

    public HashMap<Ingredient, Integer> getMeasuredIngredients() {
        HashMap<Ingredient, Integer> measuredIngredients = new HashMap<>();
        for (Ingredient i : mIngredients) {
            int recipeQuantity = mMeasuredIngredients.get(i);
            if (recipeQuantity > 0) {
                measuredIngredients.put(i, recipeQuantity);
            }
        }
        return measuredIngredients;
    }
}
