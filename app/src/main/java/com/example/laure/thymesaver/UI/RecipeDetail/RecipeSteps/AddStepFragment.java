package com.example.laure.thymesaver.UI.RecipeDetail.RecipeSteps;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.laure.thymesaver.R;

public class AddStepFragment extends BottomSheetDialogFragment {
    private EditText mStepEditText;
    private RecipeStepListener mListener;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_recipe_step, null);
        dialog.setContentView(view);
        mStepEditText = view.findViewById(R.id.new_step_edittext);

        mStepEditText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        view.findViewById(R.id.add_step_button)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String step = mStepEditText.getText().toString();
                    mStepEditText.setText("");
                    if (mListener != null) {
                        mListener.onStepAdded(step);
                    }
            }
        });
    }

    public void setListener(RecipeStepListener listener) {
        mListener = listener;
    }
}