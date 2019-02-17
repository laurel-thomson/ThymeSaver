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

public class AddShoppingItemsAdapter extends RecyclerView.Adapter<AddShoppingItemsAdapter.AddShoppingItemsViewHolder> {
    Context mContext;
    HashMap<Ingredient, Integer> mMeasuredIngredients;
    List<Ingredient> mIngredients = new ArrayList<>();

    public AddShoppingItemsAdapter(Context context) {
        mContext = context;
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
    public void onBindViewHolder(AddShoppingItemsViewHolder holder, final int position) {
        final Ingredient i = mIngredients.get(position);
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
    public AddShoppingItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);

        return new AddShoppingItemsViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    public class AddShoppingItemsViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mCheckBox;
        public TextView mNameTV;
        public TextView mUnitTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;
        public Button mDeleteButton;

        public AddShoppingItemsViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mDeleteButton.setVisibility(View.GONE);
            mUnitTV.setVisibility(View.GONE);

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    int newQuantity = mMeasuredIngredients.get(i) + 1;
                    mMeasuredIngredients.put(i, newQuantity);
                    mQuantityTV.setText(Integer.toString(newQuantity));
                }
            });

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    if (mMeasuredIngredients.get(i) == 0) return;

                    if (mMeasuredIngredients.get(i) == 1) {
                        mMeasuredIngredients.put(i, 0);
                        mCheckBox.setChecked(false);
                        return;
                    }

                    int newQuantity = mMeasuredIngredients.get(i) - 1;
                    mMeasuredIngredients.put(i, newQuantity);
                    mQuantityTV.setText(Integer.toString(newQuantity));
                }
            });

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Ingredient i = mIngredients.get(getAdapterPosition());

                    if (compoundButton.isChecked()) {
                        if (i.isBulk()) {
                            mMeasuredIngredients.put(i, 1);
                            return;
                        }

                        if (mMeasuredIngredients.get(i) == 0) {
                            mMeasuredIngredients.put(i, 1);
                            mQuantityTV.setText("1");
                        }
                        mQuantityTV.setVisibility(View.VISIBLE);
                        mIncrementer.setVisibility(View.VISIBLE);
                        mDecrementer.setVisibility(View.VISIBLE);
                    }
                    else {
                        if (i.isBulk()) {
                            mMeasuredIngredients.put(i, 0);
                            return;
                        }
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
            int quantity = mMeasuredIngredients.get(i);
            if (quantity > 0) {
                measuredIngredients.put(i, quantity);
            }
        }
        return measuredIngredients;
    }
}
