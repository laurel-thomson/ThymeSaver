package thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import thomson.laurel.beth.thymesaver.Adapters.DragHelper;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.R;

import java.util.ArrayList;
import java.util.Calendar;
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
    private Context mContext;

    public MealPlannerAdapter(Context context, MealPlanListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mealplan_item, parent, false);
                return new MealPlanViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == USER_TYPE) {

            final MealPlan mealPlan = mMealPlans.get(position);
            final TextView textView = ((MealPlanViewHolder) holder).mTextView;
            final ImageView imageView = ((MealPlanViewHolder) holder).mImageView;
            final CheckBox checkBox = ((MealPlanViewHolder) holder).mCheckBox;

            //This is necessary to make sure that when a meal plan is checked off, the meal plan
            //that takes its place isn't checked off
            textView.setPaintFlags(0);
            textView.setTextColor(Color.BLACK);
            //Kind of a hacky fix to uncheck the checkbox but not trigger a meal plan removal
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(false);

            textView.setText(mealPlan.getRecipeName());

            if (mealPlan.getImageURL() != null) {
                Picasso.with(mContext).load(mealPlan.getImageURL()).fit().centerCrop().into(imageView);
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    boolean checked = compoundButton.isChecked();
                    if (checked) {
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        textView.setTextColor(Color.GRAY);
                    }

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onMealChecked(mealPlan);
                        }
                    }, 500);
                }
            });
        }


        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
            final String scheduledDay = mMealPlans.get(position).getScheduledDay();
            headerViewHolder.sectionTitle.setText(scheduledDay);

            headerViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAddButtonClicked(scheduledDay);
                }
            });

            //Change the color of the header for the current day
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            if (days[day-1].equals(scheduledDay))
            {
                headerViewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorSecondaryText));
                headerViewHolder.sectionTitle.setTextColor(mContext.getResources().getColor(R.color.colorTextIcon));
            }
            else
            {
                headerViewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.bottom_border));
                headerViewHolder.sectionTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }
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
                }
                break;
            }
        }
    }


    public void setTouchHelper(ItemTouchHelper touchHelper) {

        this.mTouchHelper = touchHelper;
    }

    public class MealPlanViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mTextView;
        TextView mDeleteButton;
        ImageView mImageView;

        MealPlanViewHolder(View itemView) {
            super(itemView);

            mCheckBox = itemView.findViewById(R.id.mealplan_checkbox);
            mTextView = itemView.findViewById(R.id.mealplan_name);
            mDeleteButton = itemView.findViewById(R.id.mealplan_delete);
            mImageView = itemView.findViewById(R.id.mealplan_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMealClicked(mMealPlans.get(getAdapterPosition()), mImageView);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onMealDeleteClicked(mMealPlans.get(getAdapterPosition()));
                }
            });
        }
    }

    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;
        Button addButton;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.header_text);
            addButton = itemView.findViewById(R.id.header_button);
            addButton.setVisibility(View.VISIBLE);
        }
    }

    public interface MealPlanListener {
        void onMealScheduleChanged(MealPlan mealPlan);

        void onMealClicked(MealPlan mealPlan, View recipeImage);

        void onMealChecked(MealPlan mealPlan);

        void onAddButtonClicked(String scheduledDay);

        void onMealDeleteClicked(MealPlan mealPlan);
    }
}
