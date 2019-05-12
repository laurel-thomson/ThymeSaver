package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeStepListener;

import java.util.ArrayList;
import java.util.List;


public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.MyViewHolder>
            implements DragHelper.ActionCompletionContract{

    private List<String> mSteps = new ArrayList<>();
    private SparseBooleanArray mStepCheckStates = new SparseBooleanArray();
    private RecipeStepListener mListener;
    private final LayoutInflater mInflater;
    private ItemTouchHelper mTouchHelper;

    public RecipeStepAdapter(
            Context context,
            RecipeStepListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    public void setSteps(List<String> steps){

        mSteps = steps;
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        String targetStep = mSteps.get(oldPosition);
        mSteps.remove(oldPosition);
        mSteps.add(newPosition, targetStep);
        notifyItemMoved(oldPosition, newPosition);
    }


    @Override
    public void onMoveComplete(int newPosition) {
        mListener.onStepMoved(mSteps);
    }


    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.mTouchHelper = touchHelper;
    }

    @NonNull
    @Override
    public RecipeStepAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) mInflater
                .inflate(R.layout.checklist_item, parent, false);
        return new RecipeStepAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(mSteps.get(position));
        if (!mStepCheckStates.get(position, false)) {
            holder.mCheckBox.setChecked(false);}
        else {
            holder.mCheckBox.setChecked(true);
        }
    }

    public boolean[] getCheckStates() {
        boolean[] arr = new boolean[mSteps.size()];
        for (int i = 0; i < mSteps.size(); i++) {
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

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private CheckBox mCheckBox;
        private Button mDeleteButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.checklist_textview);
            mCheckBox = itemView.findViewById(R.id.checklist_checkbox);
            mDeleteButton = itemView.findViewById(R.id.checklist_delete);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean checked = compoundButton.isChecked();
                    if (checked) {
                        mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mTextView.setTextColor(Color.GRAY);
                    }
                    else {
                        mTextView.setPaintFlags(0);
                        mTextView.setTextColor(Color.BLACK);
                    }
                    mStepCheckStates.put(getAdapterPosition(), checked);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onStepDeleted(getAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onStepClicked(getAdapterPosition());
                }
            });
        }
    }
}
