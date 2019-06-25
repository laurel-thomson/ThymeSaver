package com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.RecipeIngredientsAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddIngredientFragment;
import com.example.laure.thymesaver.UI.Callbacks.ValueCallback;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import com.example.laure.thymesaver.UI.TopLevel.AddButtonFragment;
import com.example.laure.thymesaver.ViewModels.CookBookViewModel;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class RecipeIngredientsFragment extends AddButtonFragment
        implements AddRecipeIngredientListener{

    private static final int ADD_SUB_RECIPES_REQUEST = 1000;
    private RecipeDetailViewModel mViewModel;
    private RecipeIngredientsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyMessage;
    private ProgressBar mProgressBar;
    private boolean isFABOpen;
    private FloatingActionButton mAddIngredientFAB;
    private FloatingActionButton mAddSubRecipeFAB;
    private FloatingActionButton mCreateSubRecipeFAB;
    private FloatingActionButton mMenuFAB;
    private LinearLayout mFab1Layout;
    private LinearLayout mFab2Layout;
    private LinearLayout mFab3Layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new RecipeIngredientsAdapter(getActivity(),
                this);
        mEmptyMessage = view.findViewById(R.id.empty_message);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mMenuFAB = getActivity().findViewById(R.id.recipe_detail_add_button);
        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailViewModel.class);
        mViewModel.getCurrentRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe == null) return;
                observeRecipeIngredients(recipe.getSubRecipes());
            }
        });

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void observeRecipeIngredients(List<String> subRecipes) {
        mViewModel.getRecipeIngredients().observe(this, new Observer<HashMap<Ingredient, RecipeQuantity>>() {
            @Override
            public void onChanged(@Nullable HashMap<Ingredient, RecipeQuantity> ingredients) {
                if (ingredients == null) return;
                if (ingredients.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }

                mAdapter.setIngredients(ingredients, subRecipes);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFABClicked() {
        if (mViewModel.getCurrentRecipe().getValue().isSubRecipe()) {
            addIngredient();
            return;
        }

        mAddIngredientFAB = getActivity().findViewById(R.id.add_ingredient_fab);
        mAddIngredientFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                addIngredient();
            }
        });
        mAddSubRecipeFAB = getActivity().findViewById(R.id.add_sub_recipe_fab);
        mAddSubRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                addSubRecipe();
            }
        });
        mCreateSubRecipeFAB = getActivity().findViewById(R.id.create_sub_recipe_fab);
        mCreateSubRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                createNewSubRecipe();
            }
        });
        mFab1Layout = getActivity().findViewById(R.id.fab1_layout);
        mFab2Layout = getActivity().findViewById(R.id.fab2_layout);
        mFab3Layout = getActivity().findViewById(R.id.fab3_layout);

        if(!isFABOpen){
            showFABMenu();
        }else {
            closeFABMenu();
        }
    }

    private void addIngredient() {
        AddRecipeIngredientsFragment fragment = new AddRecipeIngredientsFragment();
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    private void addSubRecipe() {
        Intent intent = new Intent(getActivity(), AddSubRecipesActivity.class);
        intent.putExtra(AddSubRecipesActivity.PARENT_RECIPE, mViewModel.getCurrentRecipe().getValue().getName());
        startActivityForResult(intent, ADD_SUB_RECIPES_REQUEST);
    }

    private void createNewSubRecipe() {
        CookBookViewModel cookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mProgressBar.setVisibility(View.VISIBLE);
        cookBookViewModel.getAllRecipes(new ValueCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                promptForRecipeName(getContext(), cookBookViewModel, recipes);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void promptForRecipeName(Context context, CookBookViewModel cookBookViewModel, List<Recipe> recipes) {
        final View view = LayoutInflater.from(context).inflate(R.layout.recipe_name_dialog, null);
        final EditText nameET = view.findViewById(R.id.recipe_name_edittext);
        final AutoCompleteTextView categoryET = view.findViewById(R.id.recipe_category_edittext);
        final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Create New Recipe")
                .setPositiveButton("Create", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe recipe = new Recipe(nameET.getText().toString(), categoryET.getText().toString());
                mProgressBar.setVisibility(View.GONE);
                cookBookViewModel.addRecipe(recipe);
                mViewModel.addSubRecipe(recipe.getName());
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, recipe.getName());
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString();
                if (name.equals("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    return;
                }
                if (recipeNameExists(name, recipes)) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    textInputLayout.setError("A recipe of that name already exists.");
                    return;
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                textInputLayout.setError(null);
            }
        });

        //set up category autocomplete textview
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.recipe_categories,
                android.R.layout.select_dialog_item);
        categoryET.setAdapter(categoryAdapter);
        categoryET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Need to reset the array adapter because all elements will have
                //been cleared out when the default category is set
                ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                        view.getContext(),
                        R.array.recipe_categories,
                        android.R.layout.select_dialog_item);
                categoryET.setAdapter(categoryAdapter);
                categoryET.showDropDown();
                return true;
            }
        });
        categoryET.setText(context.getResources().getTextArray(R.array.recipe_categories)[0]);
    }

    private boolean recipeNameExists(String name, List<Recipe> recipes) {
        if (recipes == null) return false;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void setMenuFABImage(int resource) {
        mMenuFAB.setImageResource(resource);
        //This is a workaround to make the image appear after hide has been called by the view pager
        mMenuFAB.hide();
        mMenuFAB.show();
    }

    @SuppressLint("RestrictedApi")
    private void showFABMenu(){
        isFABOpen=true;
        setMenuFABImage(R.drawable.ic_clear);
        mFab1Layout.setVisibility(View.VISIBLE);
        mFab2Layout.setVisibility(View.VISIBLE);
        mFab3Layout.setVisibility(View.VISIBLE);
        mFab1Layout.animate().translationY(-getResources().getDimension(R.dimen.first_fab));
        mFab2Layout.animate().translationY(-getResources().getDimension(R.dimen.second_fab));
        mFab3Layout.animate().translationY(-getResources().getDimension(R.dimen.third_fab));
    }

    @SuppressLint("RestrictedApi")
    private void closeFABMenu(){
        isFABOpen=false;
        setMenuFABImage(android.R.drawable.ic_input_add);
        mFab1Layout.animate().translationY(0);
        mFab2Layout.animate().translationY(0);
        mFab3Layout.animate().translationY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFab1Layout.setVisibility(View.GONE);
                mFab2Layout.setVisibility(View.GONE);
                mFab3Layout.setVisibility(View.GONE); }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_SUB_RECIPES_REQUEST) {
            if (resultCode == RESULT_OK) {
                String[] subRecipes = data.getStringArrayExtra(AddSubRecipesActivity.SUB_RECIPES);
                mViewModel.addSubRecipes(subRecipes);
            }
        }
    }

    @Override
    public void onIngredientClicked(Ingredient i, RecipeQuantity quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(AddRecipeIngredientsFragment.INGREDIENT_NAME, i.getName());
        bundle.putString(AddRecipeIngredientsFragment.INGREDIENT_UNIT, quantity.getUnit());
        bundle.putDouble(AddRecipeIngredientsFragment.INGREDIENT_QUANTITY, quantity.getRecipeQuantity());
        bundle.putString(AddRecipeIngredientsFragment.INGREDIENT_SUBRECIPE, quantity.getSubRecipe());

        AddRecipeIngredientsFragment fragment = new AddRecipeIngredientsFragment();
        fragment.setArguments(bundle);
        fragment.setListener(this);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onIngredientChecked(String parentRecipe, String ingName, RecipeQuantity quantity) {
        mViewModel.addUpdateRecipeIngredient(
                parentRecipe != null ? parentRecipe : mViewModel.getCurrentRecipe().getValue().getName(),
                ingName,
                quantity);
    }

    @Override
    public void onIngredientLongClicked(Ingredient ing) {
        Bundle bundle = new Bundle();
        bundle.putString(AddIngredientFragment.INGREDIENT_NAME, ing.getName());
        bundle.putString(AddIngredientFragment.INGREDIENT_CATEGORY, ing.getCategory());
        bundle.putBoolean(AddIngredientFragment.IS_BULK, ing.isBulk());

        AddIngredientFragment fragment = new AddIngredientFragment();
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onSubRecipeDeleteClicked(String subrecipeName) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Remove " + subrecipeName + " from this recipe?")
                .setMessage("Are you sure you want to remove this subrecipe?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.removeSubRecipe(subrecipeName);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onSubRecipeClicked(String subRecipeName) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                subRecipeName);
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(final Ingredient i, final RecipeQuantity quantity) {
        String recipeName;
        if (quantity.getSubRecipe() != null) {
            recipeName = quantity.getSubRecipe();
        }
        else {
            recipeName = mViewModel.getCurrentRecipe().getValue().getName();
        }
        mViewModel.deleteRecipeIngredient(recipeName, i.getName());
        Snackbar snackbar = Snackbar
                .make(getView(), i.getName() +
                        " removed from recipe.", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.addUpdateRecipeIngredient(
                                recipeName,
                                i.getName(),
                                quantity);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Recipe ingredient restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onIngredientAddedOrUpdated(Ingredient ingredient, RecipeQuantity quantity) {
        if (quantity.getSubRecipe() == null) {
            mViewModel.addUpdateRecipeIngredient
                    (mViewModel.getCurrentRecipe().getValue().getName(),ingredient.getName(), quantity);
        }
        else {
            mViewModel.addUpdateRecipeIngredient(quantity.getSubRecipe(), ingredient.getName(), quantity);
        }
    }
}
