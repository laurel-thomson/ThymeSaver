package com.example.laure.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public abstract class MeasuredIngredientAdapter extends RecyclerView.Adapter<MeasuredIngredientAdapter.MyViewHolder>
    implements Filterable {

    Context mContext;
    HashMap<Ingredient, Integer> mMeasuredIngredients;
    List<Ingredient> mIngredients = new ArrayList<>();
    List<Ingredient> mFilteredIngredients;
    MeasuredIngredientListener mListener;
    String mUserCreatedIngredient;

    public MeasuredIngredientAdapter(
            Context context,
            MeasuredIngredientListener listener) {
        mContext = context;
        mListener = listener;
    }

    public MeasuredIngredientAdapter(Context context) {
        mContext = context;
    }

    public MeasuredIngredientAdapter(
            Context context,
            HashMap<Ingredient, Integer> measuredIngredients,
            MeasuredIngredientListener listener) {
        mContext = context;
        mMeasuredIngredients = measuredIngredients;
        for (Ingredient i : measuredIngredients.keySet()) {
            mIngredients.add(i);
        }
        mFilteredIngredients = mIngredients;
        mListener = listener;
    }

    public void setIngredients(HashMap<Ingredient, Integer> measuredIngredients) {
        mMeasuredIngredients = measuredIngredients;
        mIngredients.clear();
        for (Ingredient i : measuredIngredients.keySet()) {
            mIngredients.add(i);
        }
        mFilteredIngredients = mIngredients;
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
        final Ingredient i = mFilteredIngredients.get(position);
        holder.mNameTV.setText(i.getName());
        holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(i)));
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
                    for (Ingredient i : mIngredients) {

                        if (i.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(i);
                        }
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
        public TextView mUnitTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;
        public Button mDeleteButton;

        public MyViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
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
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
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
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    mListener.onDeleteClicked(i, mMeasuredIngredients.get(i));
                }
            });
        }
    }

    public interface MeasuredIngredientListener {
        void onIngredientQuantityChanged(Ingredient i, int quantity);

        void onIngredientCheckedOff(Ingredient i, int quantity);

        void onDeleteClicked(Ingredient i, int quantity);
    }
}
