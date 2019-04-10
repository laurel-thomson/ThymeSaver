package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recipe> mRecipes = new ArrayList<>();
    private final LayoutInflater mInflater;
    private RecipeListener mListener;
    private Context mContext;
    private static final int RECIPE_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    public RecipeAdapter(
            Context context,
            RecipeListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.checklist_item, parent, false);
                return new RecipeViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    public void setRecipes(List<Recipe> recipes) {
        //Sort the recipes
        Object[] recipeArray = recipes.toArray();
        Arrays.sort(recipeArray, (o1, o2) -> ((Recipe) o1).getName().compareTo(((Recipe) o2).getName()));
        Collections.reverse(Arrays.asList(recipeArray));

        mRecipes.clear();

        //generate category headers
        CharSequence[] categories = mContext.getResources().getStringArray(R.array.recipe_categories);
        Recipe[] headers = new Recipe[categories.length];
        for (int i = 0; i < categories.length; i++) {
            headers[i] = new Recipe("", categories[i].toString());
        }

        //add the headers in to the list
        for (Recipe r : headers) {
            mRecipes.add(r);
        }

        //add all the ingredients in under their headers
        for (Object object : recipeArray) {
            Recipe recipe = (Recipe) object;
            int position = 0;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getCategory().equals(recipe.getCategory())) {
                    position = mRecipes.indexOf(headers[i]);
                    break;
                }
            }
            mRecipes.add(position+1, recipe);
        }

        //remove any headers that don't have ingredients under them
        ListIterator<Recipe> iterator = mRecipes.listIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (getItemViewType(recipe) == HEADER_TYPE) {
                int nextIndex = iterator.nextIndex();
                if (nextIndex >= mRecipes.size() || getItemViewType(nextIndex) == HEADER_TYPE) {
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == RECIPE_TYPE) {
            RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
            holder.mTextView.setText(mRecipes.get(position).getName());
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mRecipes.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mRecipes.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    public int getItemViewType(Recipe recipe) {
        if (TextUtils.isEmpty(recipe.getName())) {
            return HEADER_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private Button mDeleteButton;
        private CheckBox mCheckBox;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.checklist_textview);
            mDeleteButton = itemView.findViewById(R.id.checklist_delete);
            mCheckBox = itemView.findViewById(R.id.checklist_checkbox);
            mCheckBox.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRecipeSelected(mRecipes.get(getAdapterPosition()));
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(mRecipes.get(getAdapterPosition()));
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

    public interface RecipeListener {
        void onRecipeSelected(Recipe recipe);

        void onDeleteClicked(Recipe recipe);
    }
}
