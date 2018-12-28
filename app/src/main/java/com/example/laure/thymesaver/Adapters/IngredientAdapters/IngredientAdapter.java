package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>
    implements Filterable {

    Context mContext;
    List<Ingredient> mIngredients;
    List<Ingredient> mFilteredIngredients;
    IngredientQuantityChangedListener mQuantityChangedListener;
    Ingredient mUserCreatedIngredient;

    public IngredientAdapter(
            Context context,
            IngredientQuantityChangedListener listener) {
        mContext = context;
        mQuantityChangedListener = listener;
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
        holder.mQuantityTV.setText(Integer.toString(ingredient.getQuantity()));
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mCheckBox;
        public TextView mNameTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;

        public MyViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.ingredient_checkbox);
            mNameTV = view.findViewById(R.id.ingredient_textview);
            mQuantityTV = view.findViewById(R.id.ingredient_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);

            //hide the checkbox in the base IngredientAdapter
            mCheckBox.setVisibility(View.INVISIBLE);

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() == 0) return;
                    i.setQuantity(i.getQuantity() - 1);
                    mQuantityChangedListener.onIngredientQuantityChanged(i, i.getQuantity());
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
                    mQuantityChangedListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface IngredientQuantityChangedListener {
        void onIngredientQuantityChanged(Ingredient ingredient, int quantity);
    }
}
