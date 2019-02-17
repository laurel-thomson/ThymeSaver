package com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListAdapter extends  RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {
    Context mContext;
    HashMap<Ingredient, Integer> mMeasuredIngredients;
    List<Ingredient> mIngredients = new ArrayList<>();
    ShoppingListListener mListener;

    public ShoppingListAdapter(Context context, ShoppingListListener listener ) {
        mContext = context;
        mListener = listener;
    }

    public void setIngredients(HashMap<Ingredient, Integer> measuredIngredients) {
        mMeasuredIngredients = measuredIngredients;
        mIngredients.clear();
        for (Ingredient i : measuredIngredients.keySet()) {
            mIngredients.add(i);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ShoppingListViewHolder holder, final int position) {
        final Ingredient i = mIngredients.get(position);
        holder.mNameTV.setText(i.getName());

        if (i.isBulk()) {
            holder.mDecrementer.setVisibility(View.GONE);
            holder.mIncrementer.setVisibility(View.GONE);
            holder.mQuantityTV.setVisibility(View.GONE);
        }
        else {
            holder.mDecrementer.setVisibility(View.VISIBLE);
            holder.mIncrementer.setVisibility(View.VISIBLE);
            holder.mQuantityTV.setVisibility(View.VISIBLE);
            holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(i)));
        }
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new ShoppingListViewHolder(itemView);
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mCheckBox;
        public TextView mNameTV;
        public TextView mUnitTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;
        public Button mDeleteButton;


        public ShoppingListViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mUnitTV.setVisibility(View.GONE);

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

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    int measuredQuantity = mMeasuredIngredients.get(i);
                    if (measuredQuantity == 1) return;
                    mMeasuredIngredients.put(i, measuredQuantity - 1);
                    mListener.onIngredientQuantityChanged(i, mMeasuredIngredients.get(i));
                    notifyDataSetChanged();
                }
            });

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    int measuredQuantity = mMeasuredIngredients.get(i);
                    if (measuredQuantity > 9999) return;
                    mMeasuredIngredients.put(i, measuredQuantity + 1);
                    mListener.onIngredientQuantityChanged(i, mMeasuredIngredients.get(i));
                    notifyDataSetChanged();
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    mListener.onDeleteClicked(i, mMeasuredIngredients.get(i));
                }
            });
        }
    }

    public interface ShoppingListListener {
        void onIngredientQuantityChanged(Ingredient i, int quantity);

        void onIngredientCheckedOff(Ingredient i, int quantity);

        void onDeleteClicked(Ingredient i, int quantity);
    }
}
