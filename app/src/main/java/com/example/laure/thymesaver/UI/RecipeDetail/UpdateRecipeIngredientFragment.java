package com.example.laure.thymesaver.UI.RecipeDetail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpdateRecipeIngredientFragment extends BottomSheetDialogFragment {
    public static final String INGREDIENT_NAME = "ingredient name" ;
    public static final String INGREDIENT_UNIT = "ingredient unit";
    public static final String INGREDIENT_QUANTITY = "ingredient quantity";
    private PantryViewModel mPantryViewModel;
    private AddRecipeIngredientListener mListener;
    private AutoCompleteTextView mNameET;
    private EditText mQuantityET;
    private AutoCompleteTextView mUnitET;
    private List<Ingredient> mTotalIngredients;
    private TextInputLayout mNameLayout;
    private TextInputLayout mQuantityLayout;
    private TextInputLayout mUnitLayout;
    private LinearLayout mDoneButton;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_recipe_ingredients, null);
        dialog.setContentView(view);

        mNameET = view.findViewById(R.id.recipe_ingredient_name);
        mUnitET = view.findViewById(R.id.ingredient_unit);
        mQuantityET = view.findViewById(R.id.recipe_quantity);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mQuantityLayout = view.findViewById(R.id.quantity_text_input_layout);
        mUnitLayout = view.findViewById(R.id.unit_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_recipe_ing_button);

        String ingredientName = getArguments().getString(INGREDIENT_NAME);
        String ingredientUnit = getArguments().getString(INGREDIENT_UNIT);
        int ingredientQuantity = getArguments().getInt(INGREDIENT_QUANTITY);

        mNameET.setText(ingredientName);
        mUnitET.setText(ingredientUnit);
        mQuantityET.setText(Integer.toString(ingredientQuantity));

        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);
        mPantryViewModel.getAllIngredients().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(@Nullable List<Ingredient> ingredients) {
                mTotalIngredients = ingredients;
                List<String> names = new ArrayList<>();
                for (Ingredient i : ingredients) {
                    names.add(i.getName());
                }
                ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(
                        view.getContext(),
                        android.R.layout.select_dialog_item,
                        names);
                mNameET.setThreshold(1);
                mNameET.setAdapter(nameAdapter);
            }
        });

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.ingredient_units,
                android.R.layout.select_dialog_item);
        mUnitET.setThreshold(0);
        mUnitET.setAdapter(unitAdapter);
        mUnitET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUnitET.showDropDown();
                return true;
            }
        });

        mUnitET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mDoneButton.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        mDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean hasError = false;

                        //check for incomplete fields
                        if (mNameET.getText().toString().equals("")) {
                            mNameLayout.setError("Ingredient name required.");
                            hasError = true;
                        }

                        if (mQuantityET.getText().toString().equals("")) {
                            mQuantityLayout.setError("Recipe quantity required.");
                            hasError = true;
                        }

                        if (hasError) return;

                        Ingredient ingredient = getIngredientFromName(mNameET.getText().toString());
                        if (ingredient == null) {
                            ingredient = new Ingredient(
                                    mNameET.getText().toString(),
                                    "Misc",
                                    true);
                            mPantryViewModel.addIngredient(ingredient);
                        }
                        RecipeQuantity quantity = new RecipeQuantity(
                                mUnitET.getText().toString(),
                                Integer.parseInt(mQuantityET.getText().toString()));
                        mListener.onIngredientUpdated(ingredient, quantity);

                        dismiss(); //close the bottom fragment after the ingredient has been updated
                    }
                });
    }

    @Nullable
    private Ingredient getIngredientFromName(String name) {
        for (Ingredient i : mTotalIngredients) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    public void setListener(AddRecipeIngredientListener listener) {
        mListener = listener;
    }
}
