package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.laure.thymesaver.Database.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>
    implements Filterable {

    private Context mContext;
    private List<Ingredient> mIngredients;
    private List<Ingredient> mFilteredIngredients;
    private IngredientAdapterListener mListener;
    private Ingredient mUserCreatedIngredient;

    public IngredientAdapter(
            Context context,
            List<Ingredient> ingredients,
            IngredientAdapterListener listener) {
        mContext = context;
        mIngredients = ingredients;
        mFilteredIngredients = ingredients;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Ingredient ingredient = mFilteredIngredients.get(position);
        holder.mNameTV.setText(ingredient.getName());
    }

    @Override
    public int getItemCount() {
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
                            mUserCreatedIngredient = new Ingredient(charString);
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
        public TextView mNameTV;

        public MyViewHolder(View view) {
            super(view);
            mNameTV = view.findViewById(R.id.ingredient_textview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected ingredient in callback
                    mListener.onIngredientSelected(mFilteredIngredients.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface IngredientAdapterListener {
        void onIngredientSelected(Ingredient ingredient);
    }
}
