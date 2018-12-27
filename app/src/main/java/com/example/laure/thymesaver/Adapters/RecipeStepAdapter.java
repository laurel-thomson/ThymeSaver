package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.laure.thymesaver.R;

import java.util.List;


public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.MyViewHolder> {

    private List<String> mSteps;
    private final LayoutInflater mInflater;
    private RecipeStepAdapter.RecipeStepAdapterListener mListener;

    public RecipeStepAdapter(
            Context context,
            List<String> steps,
            RecipeStepAdapter.RecipeStepAdapterListener listener) {
        mInflater = LayoutInflater.from(context);
        mSteps = steps;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecipeStepAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) mInflater
                .inflate(R.layout.step_list_item, parent, false);
        return new RecipeStepAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(mSteps.get(position));
    }


    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.step_list_textview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onStepSelected(mSteps.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface RecipeStepAdapterListener {
        void onStepSelected(String step);
    }
}
