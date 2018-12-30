package com.example.laure.thymesaver.Adapters.MealPlannerAdapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.SectionHeaderViewHolder;
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.SwipeAndDragHelper;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.R;

import java.util.List;

/**
 * Created by Srijith on 08-10-2017.
 */

public class MealPlannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SwipeAndDragHelper.ActionCompletionContract {
    private static final int USER_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private List<MealPlan> mMealPlans;
    private ItemTouchHelper mTouchHelper;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case USER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.planned_meal_list_item, parent, false);
                return new MealPlanViewHolder(view);
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section, parent, false);
                return new SectionHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.planned_meal_list_item, parent, false);
                return new MealPlanViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == USER_TYPE) {
            ((MealPlanViewHolder) holder).mTextView.setText(mMealPlans.get(position).getRecipeName());
            ((MealPlanViewHolder) holder).mHandle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mTouchHelper.startDrag(holder);
                    }
                    return false;
                }
            });
        } else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            headerViewHolder.sectionTitle.setText(mMealPlans.get(position).getScheduledDay());
        }
    }

    @Override
    public int getItemCount() {
        return mMealPlans == null ? 0 : mMealPlans.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mMealPlans.get(position).getRecipeName())) {
            return HEADER_TYPE;
        } else {
            return USER_TYPE;
        }
    }

    public void setMealPlans(List<MealPlan> usersList) {
        this.mMealPlans = usersList;
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        MealPlan targetMeal = mMealPlans.get(oldPosition);
        MealPlan meal = new MealPlan(targetMeal.getRecipeName(), targetMeal.getScheduledDay());
        mMealPlans.remove(oldPosition);
        mMealPlans.add(newPosition, meal);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        mMealPlans.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {

        this.mTouchHelper = touchHelper;
    }

    public class MealPlanViewHolder extends RecyclerView.ViewHolder {

        CheckBox mCheckBox;
        TextView mTextView;
        ImageView mHandle;

        public MealPlanViewHolder(View itemView) {
            super(itemView);

            mCheckBox = itemView.findViewById(R.id.planned_meal_checkbox);
            mTextView = itemView.findViewById(R.id.planned_meal_textview);
            mHandle = itemView.findViewById(R.id.planned_meal_handle);
        }
    }
}
