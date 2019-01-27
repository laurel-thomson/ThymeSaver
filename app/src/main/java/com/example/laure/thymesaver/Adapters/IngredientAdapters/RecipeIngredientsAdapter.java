package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.laure.thymesaver.R;

import java.util.HashMap;

public class RecipeIngredientsAdapter extends MeasuredIngredientAdapter {

    public RecipeIngredientsAdapter(
            Context context,
            HashMap<String, Integer> ingredients,
            MeasuredIngredientListener quantityChangedListener) {
        super(context, ingredients, quantityChangedListener);
    }

    public RecipeIngredientsAdapter(Context context, MeasuredIngredientListener quantityChangedListener ) {
        super(context, quantityChangedListener);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
}
