package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private List<Recipe> mRecipes;
    private final LayoutInflater mInflater;
    private RecipeListener mListener;

    public RecipeAdapter(
            Context context,
            RecipeListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
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
        private Button mDeleteButton;
        private CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recipe_textview);
            mDeleteButton = itemView.findViewById(R.id.recipe_delete);
            mCheckBox = itemView.findViewById(R.id.recipe_checkbox);
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

    public interface RecipeListener {
        void onRecipeSelected(Recipe recipe);

        void onDeleteClicked(Recipe recipe);
    }
}
