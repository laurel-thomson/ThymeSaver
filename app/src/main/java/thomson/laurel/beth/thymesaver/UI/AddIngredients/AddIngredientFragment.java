package thomson.laurel.beth.thymesaver.UI.AddIngredients;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.ViewModels.PantryViewModel;

public class AddIngredientFragment extends BottomSheetDialogFragment {
    private PantryViewModel mPantryViewModel;
    private AutoCompleteTextView mNameET;
    private AutoCompleteTextView mCategoryET;
    private Switch mBulkSwitch;
    private TextInputLayout mNameLayout;
    private TextInputLayout mCategoryLayout;
    private LinearLayout mDoneButton;
    private boolean mIngredientWasBulk;
    public static final String INGREDIENT_NAME = "ingredient name" ;
    public static final String INGREDIENT_CATEGORY = "ingredient category";
    public static final String IS_BULK = "ingredient is bulk";

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_ingredient, null);
        dialog.setContentView(view);

        new KeyboardUtil(getActivity(), view);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(1420);
                mNameET.requestFocus();
            }
        });

        setUpFields(view);

        if (getArguments() != null) {
            getIngredientArguments();
        }

        mPantryViewModel = ViewModelProviders.of(this).get(PantryViewModel.class);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean hasError = false;
                        String name = mNameET.getText().toString().toLowerCase();
                        String category = mCategoryET.getText().toString();
                        Boolean isBulk = mBulkSwitch.isChecked();

                        //check for incomplete fields
                        if (name.equals("")) {
                            mNameLayout.setError(getString(R.string.ingredient_name_required));
                            hasError = true;
                        }

                        if (category.equals("")) {
                            mCategoryLayout.setError(getString(R.string.ingredient_category_required));
                            hasError = true;
                        }

                        if (hasError) {
                            hideKeyboard();
                            return;
                        }

                        Ingredient ingredient = new Ingredient(name, category, isBulk);
                        saveIngredient(ingredient);

                        //clear text fields after ingredient added
                        mNameET.setText("");
                        mCategoryET.setText("");
                        mNameET.requestFocus();
                    }
                });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpFields(View view) {
        mNameET = view.findViewById(R.id.ingredient_name);
        mCategoryET = view.findViewById(R.id.ingredient_category);
        mBulkSwitch = view.findViewById(R.id.is_bulk_switch);
        mNameLayout = view.findViewById(R.id.name_text_input_layout);
        mCategoryLayout = view.findViewById(R.id.category_text_input_layout);
        mDoneButton =  view.findViewById(R.id.add_ing_button);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.ingredient_categories,
                android.R.layout.select_dialog_item);
        mCategoryET.setAdapter(categoryAdapter);
        mCategoryET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Need to repopulate the adapter because if setText() has been called, the array resources
                //will have been cleared
                ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.ingredient_categories,
                        android.R.layout.select_dialog_item);
                mCategoryET.setAdapter(categoryAdapter);
                mCategoryET.showDropDown();
                return true;
            }
        });
    }

    public void saveIngredient(Ingredient ingredient) {
        mPantryViewModel.addIngredient(ingredient);
        //if we are switching a bulk ingredient to a non-bulk, the associated shopping mod will
        //need to be update if it exists to prevent bugs in the shopping list
        if (mIngredientWasBulk && !ingredient.isBulk()) {
            mPantryViewModel.updateModToChange(ingredient.getName());
        }
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getDialog().getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    private void getIngredientArguments() {
        String ingredientName = getArguments().getString(INGREDIENT_NAME);
        String ingredientCategory = getArguments().getString(INGREDIENT_CATEGORY);
        boolean isBulk = getArguments().getBoolean(IS_BULK);
        mIngredientWasBulk = isBulk;

        mNameET.setText(ingredientName);
        mCategoryET.setText(ingredientCategory);
        mBulkSwitch.setChecked(isBulk);
        mNameET.setSelection(mNameET.getText().length());
        mCategoryET.setSelection(mCategoryET.getText().length());
    }
}
