package com.example.laure.thymesaver.UI.TopLevel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laure.thymesaver.Adapters.RecipeAdapter;
import com.example.laure.thymesaver.Models.Recipe;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import com.example.laure.thymesaver.ViewModels.CookBookViewModel;

import java.util.List;

public class CookbookFragment extends AddButtonFragment
        implements RecipeAdapter.RecipeListener {
    private CookBookViewModel mViewModel;
    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecipeAdapter(getActivity(), this);
        mViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mEmptyMessage = view.findViewById(R.id.empty_message);

        setObserver();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setObserver() {
        if (mViewModel.getAllRecipes() == null) {
            //force the main activity to close and restart
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().finish();
            startActivity(intent);
            return;
        }

        mViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setRecipes(recipes);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                recipe.getName());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(final Recipe recipe) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete " + recipe.getName() + " ?")
                .setMessage("Are you sure you want to delete this recipe? This cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.deleteRecipe(recipe);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    void launchAddItemActivity() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recipe_name_dialog, null);
        final EditText nameET = view.findViewById(R.id.recipe_name_edittext);
        final AutoCompleteTextView categoryET = view.findViewById(R.id.recipe_category_edittext);
        final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Create New Recipe")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Recipe recipe = new Recipe(nameET.getText().toString(), categoryET.getText().toString());
                        mViewModel.addRecipe(recipe);
                        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, nameET.getText().toString());
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
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
                if (mViewModel.recipeNameExists(name)) {
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
        categoryET.setText(getResources().getTextArray(R.array.recipe_categories)[0]);
    }
}
