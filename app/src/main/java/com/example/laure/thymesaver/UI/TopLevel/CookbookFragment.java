package com.example.laure.thymesaver.UI.TopLevel;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_cookbook, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.cookbook_progress);

        mViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mRecyclerView = view.findViewById(R.id.recipes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecipeAdapter(getActivity(), this);

        mViewModel.getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                //update the cached copy of recipes in the adapter
                mAdapter.setRecipes(recipes);
                progressBar.setVisibility(View.GONE);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
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

    @Override
    void launchAddItemActivity() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.recipe_name_dialog, null);
        final EditText et = view.findViewById(R.id.recipe_name_edittext);
        final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Enter Recipe Name")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, et.getText().toString());
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        et.addTextChangedListener(new TextWatcher() {
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
    }
}
