package thomson.laurel.beth.thymesaver.UI.TopLevel;

import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import thomson.laurel.beth.thymesaver.Adapters.RecipeAdapter;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.UI.RecipeImport.ImportActivity;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;

import java.util.List;

public class CookbookFragment extends AddButtonFragment
        implements RecipeAdapter.RecipeListener {
    private CookBookViewModel mViewModel;
    private RecipeAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyMessage;
    private FloatingActionButton mImportRecipeFAB;
    private FloatingActionButton mCreateRecipeFAB;
    private FloatingActionButton mMenuFAB;
    private LinearLayout mFABLayout1;
    private LinearLayout mFABLayout2;
    private boolean isFABOpen;


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
        mImportRecipeFAB = getActivity().findViewById(R.id.import_recipe_fab);
        mCreateRecipeFAB = getActivity().findViewById(R.id.create_recipe_fab);
        mMenuFAB = getActivity().findViewById(R.id.main_add_button);
        mFABLayout1 = getActivity().findViewById(R.id.fab1_layout);
        mFABLayout2 = getActivity().findViewById(R.id.fab2_layout);

        setObserver();

        mRecyclerView.setAdapter(mAdapter);
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
    public void onRecipeSelected(Recipe recipe, View recipeImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                recipe.getName());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View)recipeImage, "recipe_image");
        startActivity(intent, options.toBundle());
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

    private void promptForRecipeName(Context context, CookBookViewModel viewModel, ValueCallback<Recipe> callback) {
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
                callback.onSuccess(recipe);
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
                if (viewModel.recipeNameExists(name)) {
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onFABClicked() {
        mImportRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                importRecipe();
            }
        });

        mCreateRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                createRecipe();
            }
        });

        if(!isFABOpen){
            showFABMenu();
        }else {
            closeFABMenu();
        }
    }

    private void createRecipe() {
        promptForRecipeName(getActivity(), mViewModel, new ValueCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                mViewModel.addRecipe(recipe);
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailActivity.CURRENT_RECIPE_NAME, recipe.getName());
                startActivity(intent);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void importRecipe() {
        Intent intent = new Intent(getActivity(), ImportActivity.class);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    private void showFABMenu(){
        isFABOpen=true;
        setMenuFABImage(R.drawable.ic_clear);
        mFABLayout1.setVisibility(View.VISIBLE);
        mFABLayout2.setVisibility(View.VISIBLE);
        mFABLayout1.animate().translationY(-getResources().getDimension(R.dimen.first_fab));
        mFABLayout2.animate().translationY(-getResources().getDimension(R.dimen.second_fab));
    }

    @SuppressLint("RestrictedApi")
    private void closeFABMenu(){
        isFABOpen=false;
        setMenuFABImage(android.R.drawable.ic_input_add);
        mFABLayout1.animate().translationY(0);
        mFABLayout2.animate().translationY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFABLayout1.setVisibility(View.GONE);
                mFABLayout2.setVisibility(View.GONE);

            }
        });
    }

    private void setMenuFABImage(int resource) {
        mMenuFAB.setImageResource(resource);
        //This is a workaround to make the image appear after hide has been called by the view pager
        mMenuFAB.hide();
        mMenuFAB.show();
    }
}
