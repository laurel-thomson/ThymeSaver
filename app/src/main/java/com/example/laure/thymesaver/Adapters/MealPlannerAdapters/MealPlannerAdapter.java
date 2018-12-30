package com.example.laure.thymesaver.Adapters.MealPlannerAdapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.SectionHeaderViewHolder;
import com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper.DragHelper;
import com.example.laure.thymesaver.Models.MealPlan;
import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Code adapted from : https://github.com/sjthn/RecyclerViewDemo/tree/advanced-usecases
 */

public class MealPlannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        DragHelper.ActionCompletionContract {
    private static final int USER_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private List<MealPlan> mMealPlans = new ArrayList<>();
    private ItemTouchHelper mTouchHelper;
    private MealPlanListener mListener;

    public MealPlannerAdapter(MealPlanListener listener) {
        mListener = listener;
    }

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
            ((MealPlanViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMealClicked(mMealPlans.get(holder.getAdapterPosition()));
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

    public void setMealPlans(List<MealPlan> mealPlans) {
        mMealPlans.clear();
        MealPlan[] dayHeaders = {
                new MealPlan("","Sunday"),
                new MealPlan("","Monday"),
                new MealPlan("","Tuesday"),
                new MealPlan("","Wednesday"),
                new MealPlan("","Thursday"),
                new MealPlan("","Friday"),
                new MealPlan("","Saturday")
        };
        for (MealPlan m : dayHeaders) {
            mMealPlans.add(m);
        }
        for (MealPlan m : mealPlans) {
            int position = 0;
            for (int i = 0; i < dayHeaders.length; i++) {
                if (dayHeaders[i].getScheduledDay().equals(m.getScheduledDay())) {
                    position = mMealPlans.indexOf(dayHeaders[i]);
                    break;
                }
            }
            mMealPlans.add(position+1, m);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        //User can't drag a meal plan above the first day header
        if (newPosition == 0) {
            newPosition = 1;
        }
        MealPlan targetMeal = mMealPlans.get(oldPosition);
        mMealPlans.remove(oldPosition);
        mMealPlans.add(newPosition, targetMeal);
        notifyItemMoved(oldPosition, newPosition);
    }


    @Override
    public void onMoveComplete(int newPosition) {
        //new position can't be zero (due to if statement in onViewMoved callback)
        MealPlan currentMeal = mMealPlans.get(newPosition);
        String scheduledDay = currentMeal.getScheduledDay();
        for (int i = newPosition-1; i >= 0; i--) {
            if (getItemViewType(i) == HEADER_TYPE) {
                if (!scheduledDay.equals(mMealPlans.get(i).getScheduledDay())) {
                    currentMeal.setScheduledDate(mMealPlans.get(i).getScheduledDay());
                    mListener.onMealScheduleChanged(currentMeal);
                    break;
                }
            }
        }
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

    public interface MealPlanListener {
        void onMealScheduleChanged(MealPlan mealPlan);

        void onMealClicked(MealPlan mealPlan);
    }
}
