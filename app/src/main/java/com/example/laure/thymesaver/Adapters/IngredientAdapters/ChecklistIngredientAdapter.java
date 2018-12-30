package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class ChecklistIngredientAdapter extends MeasuredIngredientAdapter {

    public ChecklistIngredientAdapter(
            Context context,
            HashMap<String, Integer> ingredients,
            IngredientQuantityChangedListener quantityChangedListener) {
        super(context, ingredients, quantityChangedListener);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.multiselect_list_item, parent, false);
        return new ChecklistViewHolder(itemView);
    }

    public class ChecklistViewHolder extends MyViewHolder {

        public ChecklistViewHolder(View view) {
            super(view);
            mCheckBox.setVisibility(View.VISIBLE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean checked = compoundButton.isChecked();
                    if (checked) {
                        mNameTV.setPaintFlags(mNameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mNameTV.setTextColor(Color.GRAY);
                    }
                    else {
                        mNameTV.setPaintFlags(0);
                        mNameTV.setTextColor(Color.BLACK);
                    }
                }
            });
        }
    }
}
