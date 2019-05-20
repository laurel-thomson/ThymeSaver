package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.R;

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
        for (int i = 0; i < pantryList.size(); i++) {
            if (pantryList.get(i).getuId().equals(preferredId)) {
                mSelectedItem = i;
                break;
            }
        }
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
            vh.mManageButton.setText("MANAGE");
        }
        else {
            vh.mManageButton.setText("LEAVE");
        }

        if (position == mSelectedItem) {
            vh.mRadioButton.setChecked(true);
            vh.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.border));
        }
        else {
            vh.mRadioButton.setChecked(false);
            vh.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.pantry_name);
            mManageButton = itemView.findViewById(R.id.manage_pantry_button);
            mRadioButton = itemView.findViewById(R.id.pantry_radio);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    String pantryId = mPantryList.get(mSelectedItem).getuId();
                    mListener.onPreferredPantryChanged(pantryId);
                    notifyDataSetChanged();
                }
            };
            itemView.setOnClickListener(clickListener);
            mRadioButton.setOnClickListener(clickListener);

            mManageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Pantry pantry = mPantryList.get(getAdapterPosition());
                    if (pantry.isMyPantry()) {
                        //todo: on manage pantry clicked
                    }
                    else {
                        mListener.onLeavePantryClicked(pantry);
                    }
                }
            });
        }
    }


    public interface PantryListListener {
        void onPreferredPantryChanged(String pantryId);

        void onLeavePantryClicked(Pantry pantry);
    }
}
