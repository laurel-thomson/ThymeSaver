package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.MyViewHolder> {

    private SparseBooleanArray mStepCheckStates = new SparseBooleanArray();
    private Context mContext;
    private Listener mListener;
    private HashMap<Ingredient, RecipeQuantity> mRecipeQuantities = new HashMap<>();
    private List<Ingredient> mIngredients = new ArrayList<>();

    public RecipeIngredientsAdapter(Context context, Listener listener ) {
        mContext = context;
        mListener = listener;
    }

    public void setIngredients(HashMap<Ingredient, RecipeQuantity> recipeIngredients) {
        mRecipeQuantities.clear();
        for (Ingredient i : recipeIngredients.keySet()) {
            mIngredients.add(i);
            mRecipeQuantities.put(i, recipeIngredients.get(i));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Ingredient ingredient = mIngredients.get(position);

        holder.mNameTV.setText(ingredient.getName());
        holder.mQuantityTV.setText(Integer.toString(mRecipeQuantities.get(ingredient).getRecipeQuantity()));
        holder.mUnitTV.setText(mRecipeQuantities.get(ingredient).getShortUnitName());

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean checked = compoundButton.isChecked();
                if (checked) {
                    holder.mNameTV.setPaintFlags(holder.mNameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.mNameTV.setTextColor(Color.GRAY);
                }
                else {
                    holder.mNameTV.setPaintFlags(0);
                    holder.mNameTV.setTextColor(Color.BLACK);
                }
                mStepCheckStates.put(position, checked);
            }
        });
        if (!mStepCheckStates.get(position, false)) {
            holder.mCheckBox.setChecked(false);}
        else {
            holder.mCheckBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    public boolean[] getCheckStates() {
        boolean[] arr = new boolean[mIngredients.size()];
        for (int i = 0; i < mIngredients.size(); i++) {
            arr[i] = mStepCheckStates.get(i);
        }
        return arr;
    }

    public void restoreCheckStates(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            mStepCheckStates.put(i, arr[i]);
        }
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        TextView mQuantityTV;
        TextView mUnitTV;
        LinearLayout mDecrementer;
        LinearLayout mIncrementer;
        Button mDeleteButton;

        public MyViewHolder(@NonNull View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    if (i.getQuantity() == 0) return;
                    i.setQuantity(i.getQuantity() - 1);
                    mListener.onIngredientQuantityChanged(i, mRecipeQuantities.get(i));
                    notifyDataSetChanged();
                }
            });

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    if (i.getQuantity() > 100) return;
                    i.setQuantity(i.getQuantity() + 1);
                    mListener.onIngredientQuantityChanged(i, mRecipeQuantities.get(i));
                    notifyDataSetChanged();
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(
                            mIngredients.get(getAdapterPosition()),
                            mRecipeQuantities.get(mIngredients.get(getAdapterPosition())));
                }
            });
        }
    }

    public interface Listener {
        void onDeleteClicked(Ingredient i, RecipeQuantity quantity);
        void onIngredientQuantityChanged(Ingredient i, RecipeQuantity quantity);
    }
}
