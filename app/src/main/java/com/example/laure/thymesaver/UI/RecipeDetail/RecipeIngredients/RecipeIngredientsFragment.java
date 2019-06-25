package com.example.laure.thymesaver.UI.RecipeDetail.RecipeIngredients;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.IngredientAdapters.RecipeIngredientsAdapter;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.Models.RecipeQuantity;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.AddIngredients.AddIngredientFragment;
import com.example.laure.thymesaver.UI.TopLevel.AddButtonFragment;
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

        if (savedInstanceState != null) {
            boolean[] arr = (boolean[]) savedInstanceState.get("check_states");
            mAdapter.restoreCheckStates(arr);
        }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBooleanArray("check_states", mAdapter.getCheckStates());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFABClicked() {
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
        startActivityForResult(intent, ADD_SUB_RECIPES_REQUEST);
    }

    private void createNewSubRecipe() {

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
                        mViewModel.addRecipeIngredient(
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
            mViewModel.addRecipeIngredient
                    (mViewModel.getCurrentRecipe().getValue().getName(),ingredient.getName(), quantity);
        }
        else {
            mViewModel.updateSubRecipeIngredient(quantity.getSubRecipe(), ingredient.getName(), quantity);
        }
    }
}
