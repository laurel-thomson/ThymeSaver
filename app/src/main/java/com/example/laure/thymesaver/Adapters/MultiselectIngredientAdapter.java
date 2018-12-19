package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.HashMap;
import java.util.List;

public class MultiselectIngredientAdapter extends IngredientAdapter {
    public MultiselectIngredientAdapter(Context context, IngredientAdapterListener listener) {
        super(context, listener);
    }

    public MultiselectIngredientAdapter(Context context, List<Ingredient> ingredients, IngredientAdapterListener listener) {
        super(context, ingredients, listener);
    }

    public MultiselectIngredientAdapter(Context context, HashMap<Ingredient, Integer> ingredients, IngredientAdapterListener listener) {
        super(context, ingredients, listener);
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
        }
    }
}
