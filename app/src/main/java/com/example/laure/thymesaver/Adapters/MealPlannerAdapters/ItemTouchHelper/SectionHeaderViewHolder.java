package com.example.laure.thymesaver.Adapters.MealPlannerAdapters.ItemTouchHelper;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.laure.thymesaver.R;

/**
 * Created by Srijith on 08-10-2017.
 */

public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView sectionTitle;

    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        sectionTitle = itemView.findViewById(R.id.section_text);
    }

}