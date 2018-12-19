package com.example.laure.thymesaver.Adapters;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>
    implements Filterable {

    Context mContext;
    List<Ingredient> mIngredients;
    List<Ingredient> mFilteredIngredients;
    IngredientAdapterListener mListener;
    Ingredient mUserCreatedIngredient;

    public IngredientAdapter(
            Context context,
            IngredientAdapterListener listener) {
        mContext = context;
        mListener = listener;
    }

    public IngredientAdapter(
            Context context,
            List<Ingredient> ingredients,
            IngredientAdapterListener listener) {
        mContext = context;
        mIngredients = ingredients;
        mFilteredIngredients = ingredients;
        mListener = listener;
    }

    public IngredientAdapter(
            Context context,
            HashMap<Ingredient, Integer> ingredients,
            IngredientAdapterListener listener) {
        mContext = context;
        mIngredients = createMeasuredIngredientList(ingredients);
        mFilteredIngredients = mIngredients;
        mListener = listener;
    }

    private List<Ingredient> createMeasuredIngredientList(HashMap<Ingredient, Integer> measuredIngredients) {
        List<Ingredient> list = new ArrayList<>();
        for (Map.Entry<Ingredient, Integer> entry : measuredIngredients.entrySet()) {
            list.add(new MeasuredIngredient(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    private class MeasuredIngredient extends Ingredient {
        int mMeasuredQuantity;

        MeasuredIngredient(Ingredient i, int measure) {
            super(i.getName(), i.getUnit(), i.getQuantity());
            mMeasuredQuantity = measure;
        }

        @Override
        public int getQuantity() {
            return mMeasuredQuantity;
        }

        @Override
        public void setQuantity(int quantity) {
            mMeasuredQuantity = quantity;
        }
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

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
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
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() > 100) return;
                    i.setQuantity(i.getQuantity() + 1);
                    mListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface IngredientAdapterListener {
        void onIngredientQuantityChanged(Ingredient ingredient, int quantity);
    }
}
