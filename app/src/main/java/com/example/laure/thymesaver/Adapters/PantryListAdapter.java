package com.example.laure.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.R;

import java.util.List;

public class PantryListAdapter extends RecyclerView.Adapter<PantryListAdapter.MyViewHolder> {
    private List<Pantry> mPantryList;
    private Context mContext;
    private int mSelectedItem = -1;

    public PantryListAdapter(Context context) {
        mContext = context;
    }

    public void setPantryList(List<Pantry> pantryList) {
        mPantryList = pantryList;
        for (int i = 0; i < pantryList.size(); i++) {
            if (pantryList.get(i).isPreferredPantry()) {
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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        Pantry pantry = mPantryList.get(position);
        myViewHolder.mName.setText(pantry.getName());
        myViewHolder.mRadioButton.setChecked(position == mSelectedItem);
        myViewHolder.mManageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

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
                    notifyDataSetChanged();
                }
            };
            itemView.setOnClickListener(clickListener);
            mRadioButton.setOnClickListener(clickListener);
        }
    }
}