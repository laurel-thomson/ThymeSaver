package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private List<Recipe> mRecipes;
    private final LayoutInflater mInflater;
    private RecipeSelectedListener mSelectedListener;
    private RecipeCheckedListener mCheckListener;

    public RecipeAdapter(
            Context context,
            RecipeSelectedListener selectedListener,
            RecipeCheckedListener checkListener) {
        mInflater = LayoutInflater.from(context);
        mSelectedListener = selectedListener;
        mCheckListener = checkListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) mInflater
                .inflate(R.layout.recipe_list_item, parent, false);
        return new MyViewHolder(v);
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(mRecipes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recipe_list_textview);
            mCheckBox = itemView.findViewById(R.id.recipe_checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSelectedListener.onRecipeSelected(mRecipes.get(getAdapterPosition()));
                }
            });

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Recipe recipe = mRecipes.get(getAdapterPosition());
                    boolean checked = compoundButton.isChecked();
                    mCheckListener.onRecipeChecked(recipe, checked);
                }
            });
        }
    }


    public interface RecipeSelectedListener {
        void onRecipeSelected(Recipe recipe);
    }

    public interface RecipeCheckedListener {
        void onRecipeChecked (Recipe recipe, boolean checked);
    }
}
