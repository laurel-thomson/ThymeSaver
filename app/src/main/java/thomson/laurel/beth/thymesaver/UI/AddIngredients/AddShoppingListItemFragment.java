package thomson.laurel.beth.thymesaver.UI.AddIngredients;

import android.annotation.SuppressLint;
import android.app.Dialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ModType;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.ShoppingViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddShoppingListItemFragment extends BottomSheetDialogFragment {
    private PantryViewModel mPantryViewModel;
    private ShoppingViewModel mShoppingViewModel;
    private AutoCompleteTextView mNameET;
    private EditText mQuantityET;
    private List<Ingredient> mTotalIngredients = new ArrayList<>();
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
        new KeyboardUtil(getActivity(), view);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mNameET = view.findViewById(R.id.ingredient_name);
        view.findViewById(R.id.ingredient_unit).setVisibility(View.GONE);
        mQuantityET = view.findViewById(R.id.ingredient_quantity);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mQuantityLayout = view.findViewById(R.id.quantity_text_input_layout);
        mUnitLayout = view.findViewById(R.id.unit_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_recipe_ing_button);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(1300);
                //mNameET.requestFocus();
            }
        });

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
                            return;
                        }

                        Ingredient ingredient = getIngredientFromName(mNameET.getText().toString().toLowerCase());
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
                    mNameET.getText().toString().toLowerCase(),
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
}

