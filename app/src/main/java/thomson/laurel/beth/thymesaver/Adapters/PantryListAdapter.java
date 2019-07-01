package thomson.laurel.beth.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Models.Follower;
import thomson.laurel.beth.thymesaver.Models.Pantry;
import thomson.laurel.beth.thymesaver.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PantryListAdapter extends RecyclerView.Adapter<PantryListAdapter.MyViewHolder> {
    private List<Pantry> mPantryList;
    private PantryListListener mListener;
    private Context mContext;
    private int mSelectedItem = -1;

    public PantryListAdapter(Context context, PantryListListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setPantryList(List<Pantry> pantryList, String preferredId) {
        mPantryList = pantryList;
        Collections.sort(mPantryList, new Comparator<Pantry>() {
            @Override
            public int compare(Pantry p1, Pantry p2) {
                if (p1.isMyPantry()) {
                    return -1;
                }
                else if (p2.isMyPantry()) {
                    return 1;
                }
                return p1.getName().compareTo(p2.getName());
            }
        });
        for (int i = 0; i < pantryList.size(); i++) {
            if (pantryList.get(i).getuId().equals(preferredId)) {
                mSelectedItem = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pantry_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder vh, int position) {
        Pantry pantry = mPantryList.get(position);
        vh.mName.setText(pantry.getName());
        if (pantry.isMyPantry()) {
            vh.mManageButton.setText("FOLLOWERS");
        }
        else {
            vh.mManageButton.setText("LEAVE");
        }

        if (position == mSelectedItem) {
            vh.mRadioButton.setChecked(true);
            vh.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.full_border));
        }
        else {
            vh.mRadioButton.setChecked(false);
            vh.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
        }

        int noOfChildTextViews = vh.mChildItemsLayout.getChildCount();
        for (int index = 1; index < noOfChildTextViews; index++) {
            LinearLayout linearLayout = (LinearLayout) vh.mChildItemsLayout.getChildAt(index);
            linearLayout.setVisibility(View.VISIBLE);
        }

        int noOfChild = pantry.getFollowers().size();
        if (noOfChild < noOfChildTextViews) {
            for (int index = noOfChild+1; index < noOfChildTextViews; index++) {
                LinearLayout linearLayout = (LinearLayout) vh.mChildItemsLayout.getChildAt(index);
                linearLayout.setVisibility(View.GONE);
            }
        }
        for (int textViewIndex = 1; textViewIndex <= noOfChild; textViewIndex++) {
            LinearLayout linearLayout = (LinearLayout) vh.mChildItemsLayout.getChildAt(textViewIndex);
            TextView nameTV = linearLayout.findViewById(R.id.follower_name);
            nameTV.setText(pantry.getFollowers().get(textViewIndex-1).getUserName());

            TextView removeTV = linearLayout.findViewById(R.id.remove_follower);
            final Follower currentFollower = pantry.getFollowers().get(textViewIndex-1);
            removeTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRemoveFollowerClicked(currentFollower);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mPantryList == null) return 0;
        return mPantryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mManageButton;
        private RadioButton mRadioButton;
        private LinearLayout mChildItemsLayout;
        private ImageView mCollapseButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.pantry_name);
            mManageButton = itemView.findViewById(R.id.manage_pantry_button);
            mRadioButton = itemView.findViewById(R.id.pantry_radio);
            mCollapseButton = itemView.findViewById(R.id.expand_pantry_button);
            mChildItemsLayout = itemView.findViewById(R.id.ll_child_items);
            mChildItemsLayout.setVisibility(View.GONE);


            mRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedItem != getAdapterPosition()) {
                        mSelectedItem = getAdapterPosition();
                        String pantryId = mPantryList.get(mSelectedItem).getuId();
                        mListener.onPreferredPantryChanged(pantryId);
                        notifyDataSetChanged();
                    }
                }
            });

            int intMaxNoOfChild = 0;
            for (int index = 0; index < mPantryList.size(); index++) {
                int intMaxSizeTemp = mPantryList.get(index).getFollowers().size();
                if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp;
            }
            for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.follower_list_item, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mChildItemsLayout.addView(view, layoutParams);
            }

            mManageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pantry pantry = mPantryList.get(getAdapterPosition());
                    if (pantry.isMyPantry()) {
                        mChildItemsLayout.setVisibility(View.VISIBLE);
                        mManageButton.setVisibility(View.GONE);
                        mCollapseButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        mListener.onLeavePantryClicked(pantry);
                    }

                }
            });

            mCollapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChildItemsLayout.setVisibility(View.GONE);
                    mCollapseButton.setVisibility(View.GONE);
                    mManageButton.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    public interface PantryListListener {
        void onPreferredPantryChanged(String pantryId);

        void onLeavePantryClicked(Pantry pantry);

        void onRemoveFollowerClicked(Follower follower);
    }
}
