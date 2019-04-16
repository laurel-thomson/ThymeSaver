package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class AddShoppingItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context mContext;
    private HashMap<Ingredient, Integer> mMeasuredIngredients;
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<Ingredient> mFilteredIngredients = mIngredients;
    private static final int INGREDIENT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    public AddShoppingItemsAdapter(Context context) {
        mContext = context;
    }

    public void setIngredients(HashMap<Ingredient, Integer> measuredIngredients) {
        mMeasuredIngredients = measuredIngredients;

        mIngredients.clear();

        //generate category headers
        CharSequence[] categories = mContext.getResources().getStringArray(R.array.ingredient_categories);
        Ingredient[] headers = new Ingredient[categories.length];
        for (int i = 0; i < categories.length; i++) {
            headers[i] = new Ingredient("", categories[i].toString(), false);
        }

        //add the headers in to the list
        for (Ingredient i : headers) {
            mIngredients.add(i);
        }

        //sort the ingredients
        Object[] keys = measuredIngredients.keySet().toArray();
        Arrays.sort(keys, (o1, o2) -> ((Ingredient) o1).getName().compareTo(((Ingredient) o2).getName()));
        Collections.reverse(Arrays.asList(keys));

        //add all the ingredients in under their headers
        for (Object object : keys) {
            Ingredient ingredient = (Ingredient) object;
            int position = 0;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getCategory().equals(ingredient.getCategory())) {
                    position = mIngredients.indexOf(headers[i]);
                    break;
                }
            }
            mIngredients.add(position+1, ingredient);
        }

        //remove any headers that don't have ingredients under them
        ListIterator<Ingredient> iterator = mIngredients.listIterator();
        while (iterator.hasNext()) {
            Ingredient ingredient = iterator.next();
            if (getItemViewType(ingredient) == HEADER_TYPE) {
                int nextIndex = iterator.nextIndex();
                if (nextIndex >= mIngredients.size() || getItemViewType(nextIndex) == HEADER_TYPE) {
                    iterator.remove();
                }
            }
        }

        mFilteredIngredients = mIngredients;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == INGREDIENT_TYPE) {
            final Ingredient i = mFilteredIngredients.get(position);
            final AddShoppingItemsViewHolder holder = (AddShoppingItemsViewHolder) viewHolder;
            holder.mNameTV.setText(i.getName());
            holder.mCheckBox.setChecked(mMeasuredIngredients.get(i) > 0);
            if (holder.mCheckBox.isChecked()) {
                holder.mQuantityTV.setText(Integer.toString(mMeasuredIngredients.get(i)));
            } else {
                holder.mQuantityTV.setText("0");
                holder.mQuantityTV.setVisibility(View.GONE);
                holder.mIncrementer.setVisibility(View.GONE);
                holder.mDecrementer.setVisibility(View.GONE);
            }
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mFilteredIngredients.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INGREDIENT_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.ingredient_list_item, parent, false);
                return new AddShoppingItemsViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mFilteredIngredients.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return INGREDIENT_TYPE;
        }
    }

    public int getItemViewType(Ingredient ingredient) {
        if (TextUtils.isEmpty(ingredient.getName())) {
            return HEADER_TYPE;
        } else {
            return INGREDIENT_TYPE;
        }
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
                    for (Ingredient ing : mIngredients) {

                        if (ing.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(ing);
                        }
                    }

                    //if we didn't match any ingredients, we want to add what the user is typing
                    //as an ingredient
                    //if (filteredList.size() == 0) {
                       // mUserCreatedIngredient = charString;
                        //filteredList.add(mUserCreatedIngredient);
                    //}

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

    public class AddShoppingItemsViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mCheckBox;
        public TextView mNameTV;
        public TextView mQuantityTV;
        public LinearLayout mDecrementer;
        public LinearLayout mIncrementer;
        public Button mDeleteButton;

        public AddShoppingItemsViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mDeleteButton.setVisibility(View.GONE);

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    int newQuantity = mMeasuredIngredients.get(i) + 1;
                    mMeasuredIngredients.put(i, newQuantity);
                    mQuantityTV.setText(Integer.toString(newQuantity));
                }
            });

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
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
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());

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
        for (Ingredient i : mFilteredIngredients) {
            if (getItemViewType(i) == HEADER_TYPE) continue;
            int quantity = mMeasuredIngredients.get(i);
            if (quantity > 0) {
                measuredIngredients.put(i, quantity);
            }
        }
        return measuredIngredients;
    }

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.header_text);
        }
    }
}
