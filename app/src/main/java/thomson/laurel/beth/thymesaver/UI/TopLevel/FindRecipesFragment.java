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

public class FindRecipesFragment extends AddButtonFragment implements RecipeAdapter.RecipeListener {
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
        return inflater.inflate(R.layout.fragment_find_recipes, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mAdapter = new RecipeAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

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
    public void onDeleteClicked(Recipe recipe) {

    }

    @Override
    public void onFABClicked() {

    }
}
