package com.example.laure.thymesaver.UI.AddIngredients;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddShoppingListItemFragment extends BottomSheetDialogFragment {
    private PantryViewModel mPantryViewModel;
    private ShoppingViewModel mShoppingViewModel;
    private AutoCompleteTextView mNameET;
    private EditText mQuantityET;
    private List<Ingredient> mTotalIngredients;
    private TextInputLayout mNameLayout;
    private TextInputLayout mQuantityLayout;
    private TextInputLayout mUnitLayout;
    private LinearLayout mDoneButton;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_measured_ingredient, null);
        dialog.setContentView(view);

        mNameET = view.findViewById(R.id.ingredient_name);
        view.findViewById(R.id.ingredient_unit).setVisibility(View.GONE);
        mQuantityET = view.findViewById(R.id.ingredient_quantity);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mQuantityLayout = view.findViewById(R.id.quantity_text_input_layout);
        mUnitLayout = view.findViewById(R.id.unit_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_recipe_ing_button);

        mQuantityET.setText("1");
        mQuantityET.setInputType(InputType.TYPE_CLASS_NUMBER);

        mShoppingViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
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
                mNameET.setThreshold(0);
                mNameET.setAdapter(nameAdapter);
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

                        if (hasError) {
                            hideKeyboard();
                            return;
                        }

                        Ingredient ingredient = getIngredientFromName(mNameET.getText().toString());
                        saveIngredient(ingredient);

                        //clear text fields after ingredient added
                        mNameET.setText("");
                        mQuantityET.setText("1");
                        mNameET.requestFocus();
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

    public void saveIngredient(Ingredient ingredient) {
        int quantity;
        if (ingredient == null) {
            quantity = Integer.parseInt(mQuantityET.getText().toString());
            mShoppingViewModel.addShoppingModification(
                    mNameET.getText().toString(),
                    ModType.NEW,
                    quantity);
        }
        else if (ingredient.isBulk()) {
            mShoppingViewModel.addShoppingModification(ingredient.getName(), ModType.ADD, 0);
        }
        else {
            quantity = Integer.parseInt(mQuantityET.getText().toString());
            mShoppingViewModel.addShoppingModification(
                    ingredient.getName(),
                    ModType.CHANGE,
                    quantity);
        }
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getDialog().getWindow().getCurrentFocus().getWindowToken(), 0);
    }
}
