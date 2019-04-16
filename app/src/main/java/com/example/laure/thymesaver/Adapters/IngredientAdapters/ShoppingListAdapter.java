package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class ShoppingListAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    HashMap<Ingredient, Integer> mMeasuredIngredients;
    List<Ingredient> mIngredients = new ArrayList<>();
    ShoppingListListener mListener;
    private static final int INGREDIENT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    public ShoppingListAdapter(Context context, ShoppingListListener listener ) {
        mContext = context;
        mListener = listener;
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

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INGREDIENT_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.ingredient_list_item, parent, false);
                return new ShoppingListViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == INGREDIENT_TYPE) {
            final Ingredient i = mIngredients.get(position);
            ShoppingListViewHolder holder = (ShoppingListViewHolder) viewHolder;

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
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mIngredients.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mIngredients.get(position).getName())) {
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

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        TextView mQuantityTV;
        LinearLayout mDecrementer;
        LinearLayout mIncrementer;
        Button mDeleteButton;

        ShoppingListViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Ingredient ing = mIngredients.get(getAdapterPosition());
                    mListener.onIngredientCheckedOff(
                            ing,
                            mMeasuredIngredients.get(ing));
                }
            });

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient i = mIngredients.get(getAdapterPosition());
                    int measuredQuantity = mMeasuredIngredients.get(i);
                    if (measuredQuantity == 1) return;
                    mMeasuredIngredients.put(i, measuredQuantity - 1);
                    mListener.onIngredientQuantityChanged(i, -1);
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
                    mListener.onIngredientQuantityChanged(i, 1);
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

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        Button addButton;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.header_text);
            addButton = itemView.findViewById(R.id.header_add_button);
            addButton.setVisibility(View.GONE);
        }
    }

    public interface ShoppingListListener {
        void onIngredientQuantityChanged(Ingredient i, int quantity);

        void onIngredientCheckedOff(Ingredient i, int quantity);

        void onDeleteClicked(Ingredient i, int quantity);
    }
}
