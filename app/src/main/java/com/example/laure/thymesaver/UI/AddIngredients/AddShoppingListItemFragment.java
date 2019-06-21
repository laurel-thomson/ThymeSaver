package com.example.laure.thymesaver.UI.AddIngredients;

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
import com.example.laure.thymesaver.Models.ModType;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients.AddRecipeIngredientListener;
import com.example.laure.thymesaver.ViewModels.PantryViewModel;
import com.example.laure.thymesaver.ViewModels.ShoppingViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddShoppingListItemFragment extends BottomSheetDialogFragment {
    private PantryViewModel mPantryViewModel;
    private ShoppingViewModel mShoppingViewModel;
    private AddShoppingListItemListener mListener;
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
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_recipe_ingredients, null);
        dialog.setContentView(view);

        mNameET = view.findViewById(R.id.recipe_ingredient_name);
        view.findViewById(R.id.ingredient_unit).setVisibility(View.GONE);
        mQuantityET = view.findViewById(R.id.recipe_quantity);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mQuantityLayout = view.findViewById(R.id.quantity_text_input_layout);
        mUnitLayout = view.findViewById(R.id.unit_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_recipe_ing_button);

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
                mNameET.setThreshold(1);
                mNameET.setAdapter(nameAdapter);
            }
        });

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.ingredient_units,
                android.R.layout.select_dialog_item);

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
                        saveIngredient(ingredient);

                        //clear text fields after ingredient added
                        mNameET.setText("");
                        mQuantityET.setText("");
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
        Ingredient newIngredient;
        int quantity;
        if (ingredient == null) {
            newIngredient = new Ingredient(
                    mNameET.getText().toString(),
                    "Misc",
                    false
            );
            quantity = Integer.parseInt(mQuantityET.getText().toString());
            mShoppingViewModel.addShoppingModification(
                    mNameET.getText().toString(),
                    ModType.NEW,
                    quantity);
        }
        else if (ingredient.isBulk()) {
            quantity = 0;
            mShoppingViewModel.addShoppingModification(ingredient.getName(), ModType.ADD, 0);
            newIngredient = ingredient;
        }
        else {
            quantity = Integer.parseInt(mQuantityET.getText().toString());
            mShoppingViewModel.addShoppingModification(
                    ingredient.getName(),
                    ModType.CHANGE,
                    quantity);
            newIngredient = ingredient;
        }
        //call listener
        mListener.onIngredientAdded(ingredient, quantity);
    }

    public void setListener(AddShoppingListItemListener listener) {
        mListener = listener;
    }
}
