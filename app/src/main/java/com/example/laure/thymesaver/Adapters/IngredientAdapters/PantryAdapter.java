package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.BulkIngredientStates;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.MyViewHolder>
    implements Filterable {

    Context mContext;
    List<Ingredient> mIngredients;
    List<Ingredient> mFilteredIngredients;
    IngredientListener mListener;
    Ingredient mUserCreatedIngredient;

    public PantryAdapter(
            Context context,
            IngredientListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mIngredients = ingredients;
        mFilteredIngredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.ingredient_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Ingredient ingredient = mFilteredIngredients.get(position);
        holder.mNameTV.setText(ingredient.getName());
        holder.mUnitTV.setVisibility(View.GONE);

        //Bulk ingredients
        if (ingredient.isBulk()) {
            holder.mDecrementer.setVisibility(View.GONE);
            holder.mIncrementer.setVisibility(View.GONE);
            holder.mQuantityTV.setPadding(0,0,35,0);

            if (ingredient.getQuantity() == BulkIngredientStates
                    .convertEnumToInt(BulkIngredientStates.OUT_OF_STOCK)) {

                holder.mQuantityTV.setText("Out of stock");
                holder.mQuantityTV.setTextColor(getAccentColor());
            }
            else if (ingredient.getQuantity() == BulkIngredientStates
                    .convertEnumToInt(BulkIngredientStates.RUNNING_LOW)) {

                holder.mQuantityTV.setText("Running low");
                holder.mQuantityTV.setTextColor(getAccentColor());
            }
            else {
                holder.mQuantityTV.setText("In stock");
                holder.mQuantityTV.setTextColor(Color.parseColor("#000000"));
            }
        }

        //Non-bulk ingredients
        else {
            holder.mQuantityTV.setText(Integer.toString(ingredient.getQuantity()));
            holder.mDecrementer.setVisibility(View.VISIBLE);
            holder.mIncrementer.setVisibility(View.VISIBLE);
            holder.mQuantityTV.setTextColor(Color.parseColor("#000000"));
        }
    }

    private int getAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    @Override
    public int getItemCount() {
        if (mFilteredIngredients == null) return 0;
        return mFilteredIngredients.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredIngredients = mIngredients;
                } else {
                    List<Ingredient> filteredList = new ArrayList<>();
                    for (Ingredient row : mIngredients) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    //if we didn't match any ingredients, we want to add what the user is typing
                    //as an ingredient
                    if (filteredList.size() == 0) {
                        if (mUserCreatedIngredient == null) {
                            mUserCreatedIngredient = new Ingredient();
                            mUserCreatedIngredient.setName(charString);
                        }
                        else {
                            mUserCreatedIngredient.setName(charString);
                        }
                        filteredList.add(mUserCreatedIngredient);
                    }

                    mFilteredIngredients = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredIngredients;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredIngredients = (ArrayList<Ingredient>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        TextView mQuantityTV;
        TextView mUnitTV;
        LinearLayout mDecrementer;
        LinearLayout mIncrementer;
        Button mDeleteButton;

        MyViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mCheckBox.setVisibility(View.GONE);
            mUnitTV.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient ingredient = mFilteredIngredients.get(getAdapterPosition());
                    mListener.onIngredientClicked(ingredient);
                }
            });

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() == 0) return;
                    i.setQuantity(i.getQuantity() - 1);
                    mListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() > 100) return;
                    i.setQuantity(i.getQuantity() + 1);
                    mListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(mFilteredIngredients.get(getAdapterPosition()));
                }
            });

            mQuantityTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient ingredient = mFilteredIngredients.get(getAdapterPosition());
                    if (!ingredient.isBulk()) return;

                    int newQuantity = BulkIngredientStates.getNextStateAsInt(ingredient.getQuantity());
                    mListener.onIngredientQuantityChanged(ingredient, newQuantity);
                }
            });
        }
    }

    public interface IngredientListener {
        void onIngredientQuantityChanged(Ingredient ingredient, int quantity);

        void onDeleteClicked(Ingredient ingredient);

        void onIngredientClicked(Ingredient ingredient);
    }
}
