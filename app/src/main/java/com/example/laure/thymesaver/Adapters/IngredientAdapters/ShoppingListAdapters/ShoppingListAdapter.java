package com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class ShoppingListAdapter extends MeasuredIngredientAdapter {

    public ShoppingListAdapter(Context context, MeasuredIngredientListener quantityChangedListener ) {
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
            mDeleteButton.setVisibility(View.GONE);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Ingredient ing = mIngredients.get(getAdapterPosition());
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
