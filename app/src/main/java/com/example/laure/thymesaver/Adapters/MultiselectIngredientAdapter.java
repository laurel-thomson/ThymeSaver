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
                    }
                    else {
                        mSelectedMeasuredIngredients.remove(i);
                    }
                }
            });
        }
    }
}
