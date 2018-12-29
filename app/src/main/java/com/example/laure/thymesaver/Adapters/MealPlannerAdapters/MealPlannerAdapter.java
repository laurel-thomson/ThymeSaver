package com.example.laure.thymesaver.Adapters.MealPlannerAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laure.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MealPlannerAdapter extends RecyclerView.Adapter<MealPlannerAdapter.SimpleViewHolder> {
    private final Context mContext;
    private List<String> mData;

    public void add(String s,int position) {
        position = position == -1 ? getItemCount()  : position;
        mData.add(position,s);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position < getItemCount()  ) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final CheckBox checkbox;

        public SimpleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.recipe_list_textview);
            checkbox = view.findViewById(R.id.recipe_checkbox);
        }
    }

    public MealPlannerAdapter(Context context, String[] data) {
        mContext = context;
        if (data != null)
            mData = new ArrayList<>(Arrays.asList(data));
        else mData = new ArrayList<>();
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        if (position < 0 || position >= mData.size()) return;
        holder.title.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
