package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class ChecklistIngredientAdapter extends MeasuredIngredientAdapter {

    public ChecklistIngredientAdapter(
            Context context,
            HashMap<String, Integer> ingredients,
            IngredientAdapterListener quantityChangedListener) {
        super(context, ingredients, quantityChangedListener);
    }

    public ChecklistIngredientAdapter(Context context, IngredientAdapterListener quantityChangedListener ) {
        super(context, quantityChangedListener);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new ChecklistViewHolder(itemView);
    }

    public class ChecklistViewHolder extends MyViewHolder {

        public ChecklistViewHolder(View view) {
            super(view);
            mCheckBox.setVisibility(View.VISIBLE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String ing = mIngredients.get(getAdapterPosition());
                    mListener.onIngredientCheckedOff(
                            ing,
                            mMeasuredIngredients.get(ing));
                    if (compoundButton.isChecked()) {
                        compoundButton.setChecked(false);
                    }
                }
            });
        }
    }
}
