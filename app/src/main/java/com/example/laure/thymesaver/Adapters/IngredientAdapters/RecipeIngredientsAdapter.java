package com.example.laure.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients.AddRecipeIngredientListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SparseBooleanArray mStepCheckStates = new SparseBooleanArray();
    private Context mContext;
    private AddRecipeIngredientListener mListener;
    private HashMap<Ingredient, RecipeQuantity> mRecipeQuantities = new HashMap<>();
    private List<Ingredient> mIngredients = new ArrayList<>();
    private static final int INGREDIENT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    public RecipeIngredientsAdapter(Context context, AddRecipeIngredientListener listener ) {
        mContext = context;
        mListener = listener;
    }

    public void setIngredients(HashMap<Ingredient, RecipeQuantity> recipeIngredients) {
        mRecipeQuantities.clear();
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
        Object[] keys = recipeIngredients.keySet().toArray();
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
            mRecipeQuantities.put(ingredient, recipeIngredients.get(ingredient));
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INGREDIENT_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.recipe_ingredient_list_item, parent, false);
                return new IngredientViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == INGREDIENT_TYPE) {
            Ingredient ingredient = mIngredients.get(position);
            final IngredientViewHolder holder = (IngredientViewHolder) viewHolder;
            holder.mNameTV.setText(ingredient.getName());
            holder.mQuantityTV.setText(Integer.toString(mRecipeQuantities.get(ingredient).getRecipeQuantity()));
            holder.mUnitTV.setText(mRecipeQuantities.get(ingredient).getUnit());

            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean checked = compoundButton.isChecked();
                    if (checked) {
                        holder.mNameTV.setPaintFlags(holder.mNameTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.mNameTV.setTextColor(Color.GRAY);
                    } else {
                        holder.mNameTV.setPaintFlags(0);
                        holder.mNameTV.setTextColor(Color.BLACK);
                    }
                    mStepCheckStates.put(position, checked);
                }
            });
            if (!mStepCheckStates.get(position, false)) {
                holder.mCheckBox.setChecked(false);
            } else {
                holder.mCheckBox.setChecked(true);
            }
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mIngredients.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
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


    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        TextView mQuantityTV;
        TextView mUnitTV;
        LinearLayout mDecrementer;
        LinearLayout mIncrementer;
        Button mDeleteButton;

        public IngredientViewHolder(@NonNull View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mUnitTV = view.findViewById(R.id.ing_unit_label);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mDecrementer.setVisibility(View.GONE);
            mIncrementer.setVisibility(View.GONE);

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(
                            mIngredients.get(getAdapterPosition()),
                            mRecipeQuantities.get(mIngredients.get(getAdapterPosition())));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onIngredientClicked(
                            mIngredients.get(getAdapterPosition()),
                            mRecipeQuantities.get(mIngredients.get(getAdapterPosition())));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onIngredientLongClicked(mIngredients.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.header_text);
        }
    }
}
