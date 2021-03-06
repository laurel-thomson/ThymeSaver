package thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeIngredients;

import android.annotation.SuppressLint;
import android.app.Dialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.AddIngredients.KeyboardUtil;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeIngredientsFragment extends BottomSheetDialogFragment {
    private PantryViewModel mPantryViewModel;
    private AddRecipeIngredientListener mListener;
    private AutoCompleteTextView mNameET;
    private EditText mQuantityET;
    private EditText mUnitET;
    private List<Ingredient> mTotalIngredients;
    private TextInputLayout mNameLayout;
    private TextInputLayout mQuantityLayout;
    private TextInputLayout mUnitLayout;
    private LinearLayout mDoneButton;
    private String mSubRecipe;
    public static final String INGREDIENT_NAME = "ingredient name" ;
    public static final String INGREDIENT_UNIT = "ingredient unit";
    public static final String INGREDIENT_QUANTITY = "ingredient quantity";
    public static final String INGREDIENT_SUBRECIPE = "ingredient sub recipe";

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_measured_ingredient, null);
        dialog.setContentView(view);

        new KeyboardUtil(getActivity(), view);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(1500);
                mNameET.requestFocus();
            }
        });

        mNameET = view.findViewById(R.id.ingredient_name);
        mUnitET = view.findViewById(R.id.ingredient_unit);
        mQuantityET = view.findViewById(R.id.ingredient_quantity);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mQuantityLayout = view.findViewById(R.id.quantity_text_input_layout);
        mUnitLayout = view.findViewById(R.id.unit_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_recipe_ing_button);

        if (getArguments() != null) {
            getIngredientArguments();
        }
        else {
            mQuantityET.setText("1");
            mQuantityET.setSelection(mQuantityET.getText().length());
        }

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
                            mNameLayout.setError(getString(R.string.ingredient_name_required));
                            hasError = true;
                        }

                        if (mQuantityET.getText().toString().equals("")) {
                            mQuantityLayout.setError(getString(R.string.recipe_quantity_required));
                            hasError = true;
                        }

                        double quantity;

                        try {
                            quantity = Double.parseDouble(mQuantityET.getText().toString());
                        }
                        catch (NumberFormatException e) {
                            mQuantityLayout.setError(getString(R.string.incorrect_number_format));
                            quantity = -1;
                            hasError = true;
                        }

                        if (hasError) {
                            hideKeyboard();
                            return;
                        }

                        String ingName = mNameET.getText().toString().toLowerCase();
                        Ingredient ingredient = getIngredientFromName(ingName);
                        if (ingredient == null) {
                            ingredient = new Ingredient(
                                    ingName,
                                    "Misc",
                                    true);
                            mPantryViewModel.addIngredient(ingredient);
                        }
                        RecipeQuantity recipeQuantity = new RecipeQuantity(
                                mUnitET.getText().toString(),
                                quantity);
                        recipeQuantity.setSubRecipe(mSubRecipe);
                        mListener.onIngredientAddedOrUpdated(ingredient, recipeQuantity);

                        //clear text fields after ingredient added
                        mNameET.setText("");
                        mQuantityET.setText("1");
                        mQuantityET.setSelection(mQuantityET.getText().length());
                        mUnitET.setText("");
                        mNameET.requestFocus();
                    }
                });
    }

    private void getIngredientArguments() {
        String ingredientName = getArguments().getString(INGREDIENT_NAME);
        String ingredientUnit = getArguments().getString(INGREDIENT_UNIT);
        double ingredientQuantity = getArguments().getDouble(INGREDIENT_QUANTITY);
        mSubRecipe = getArguments().getString(INGREDIENT_SUBRECIPE);

        mNameET.setText(ingredientName);
        mUnitET.setText(ingredientUnit);
        if (ingredientQuantity % 1 == 0) {
            mQuantityET.setText((int) ingredientQuantity + "");
        }
        else {
            mQuantityET.setText(Double.toString(ingredientQuantity));
        }
        mNameET.setSelection(mNameET.getText().length());
        mUnitET.setSelection(mUnitET.getText().length());
        mQuantityET.setSelection(mQuantityET.getText().length());
    }

    @Nullable
    private Ingredient getIngredientFromName(String name) {
        for (Ingredient i : mTotalIngredients) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getDialog().getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    public void setListener(AddRecipeIngredientListener listener) {
        mListener = listener;
    }
}
