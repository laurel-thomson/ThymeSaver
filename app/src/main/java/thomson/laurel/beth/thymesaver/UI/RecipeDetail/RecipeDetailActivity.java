package thomson.laurel.beth.thymesaver.UI.RecipeDetail;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import thomson.laurel.beth.thymesaver.Database.Firebase.StorageRepository;
import thomson.laurel.beth.thymesaver.Database.IStorageRepository;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.CategoryEditText;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeIngredients.RecipeIngredientsFragment;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeSteps.RecipeStepsFragment;
import thomson.laurel.beth.thymesaver.UI.TopLevel.ThymesaverFragment;
import thomson.laurel.beth.thymesaver.ViewModels.CookBookViewModel;
import thomson.laurel.beth.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeDetailActivity extends AppCompatActivity{

    public static String CURRENT_RECIPE_NAME = "Current Recipe Name";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ViewPager mViewPager;
    private RecipeDetailViewModel mRecipeDetailViewModel;
    private CookBookViewModel mCookBookViewModel;
    private FloatingActionButton mFAB;
    private ViewPagerAdapter mAdapter;
    private IStorageRepository mStorageRepository;
    private String mRecipeName;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mStorageRepository = StorageRepository.getInstance();
        mRecipeDetailViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        mCookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        mRecipeName= getIntent().getStringExtra(CURRENT_RECIPE_NAME);
        mRecipeDetailViewModel.setCurrentRecipe(mRecipeName);
        mRecipeDetailViewModel.getCurrentRecipe(new ValueCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                if (recipe.getImageURL() != null) {
                    setRecipeImage(recipe.getImageURL());

                } else {
                    supportStartPostponedEnterTransition();
                }

                if (recipe.getSourceURL() != null) {
                    TextView sourceUrl = findViewById(R.id.source_url);
                    sourceUrl.setText(recipe.getSourceURL());
                }
            }

            @Override
            public void onError(String error) {

            }
        });


        setUpActionBar(mRecipeName);
        mViewPager = findViewById(R.id.pager);

        //prevents the view pager from recreating the Recipe Steps fragment, which would remove
        //the checks from the checkboxes
        mViewPager.setOffscreenPageLimit(2);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        mFAB.show();
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                    case ViewPager.SCROLL_STATE_SETTLING:
                        mFAB.hide();
                        break;
                }
            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        mFAB = findViewById(R.id.recipe_detail_add_button);
        mFAB.setOnClickListener(view -> {
            ThymesaverFragment currentFragment = (ThymesaverFragment) mAdapter.getItem(
                    mViewPager.getCurrentItem());
            currentFragment.onFABClicked();
        });

        findViewById(R.id.camera_button).setOnClickListener(view -> dispatchTakePictureIntent());
    }

    private void restartActivity(String recipeName) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(CURRENT_RECIPE_NAME, recipeName);
        finish();
        startActivity(intent);
    }

    private void setRecipeImage(String imageURL) {
        ImageView headerImage = findViewById(R.id.recipe_header_image);
        if (isNetworkAvailable()) {
            Picasso.with(this).load(imageURL).fit().centerCrop().into(headerImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            if (mProgressBar != null) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        else {
            supportStartPostponedEnterTransition();
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mProgressBar = findViewById(R.id.recycler_view_progress);
                mProgressBar.setVisibility(View.VISIBLE);
                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                mStorageRepository.uploadImage(photo, mRecipeName, new ValueCallback<String>() {
                    @Override
                    public void onSuccess(String downloadURL) {
                        Recipe recipe = mRecipeDetailViewModel.getCurrentRecipe();
                        recipe.setImageURL(downloadURL);
                        mRecipeDetailViewModel.updateRecipe();
                        setRecipeImage(downloadURL);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUpActionBar(String recipeName) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle(recipeName);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
        ImageView recipeImage = findViewById(R.id.recipe_header_image);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener( new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percentage = 1 - ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                recipeImage.setAlpha(percentage);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editRecipe();
                return true;
            case R.id.action_renew:
                clearAllChecks();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void renameRecipe(Recipe recipe) {
        mRecipeDetailViewModel.renameRecipe(recipe.getName(), new thomson.laurel.beth.thymesaver.UI.Callbacks.Callback() {
            @Override
            public void onSuccess() {
                updateCategories(recipe);
                restartActivity(recipe.getName());
            }

            @Override
            public void onError(String err) {

            }
        });
    }

    private List<String> checkForRemovedCategories(Recipe recipe) {
        Recipe oldRecipe = mRecipeDetailViewModel.getCurrentRecipe();
        if (recipe.getCategories().size() < oldRecipe.getCategories().size()) {
            List<String> removedCategories = new ArrayList<>();
            for (String category : oldRecipe.getCategories()) {
                if (!recipe.getCategories().contains(category)) {
                    removedCategories.add(category);
                }
            }
            return removedCategories;
        }
        return null;
    }

    private void updateCategories(Recipe recipe) {
        mRecipeDetailViewModel.updateCategories(recipe.getName(), recipe.getCategories(), checkForRemovedCategories(recipe));
        restartActivity(recipe.getName());
    }

    private void editRecipe() {
        CookBookViewModel cookBookViewModel = ViewModelProviders.of(this).get(CookBookViewModel.class);
        Context context = this;
        cookBookViewModel.getAllRecipes(new ValueCallback<List<Recipe>>() {
            @Override
            public void onSuccess(List<Recipe> recipes) {
                promptForRecipeEdit(context, recipes, new ValueCallback<Recipe>() {
                    @Override
                    public void onSuccess(Recipe recipe) {
                        if (!recipe.getName().equals(mRecipeName)) {
                            renameRecipe(recipe);
                        } else {
                            updateCategories(recipe);
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private boolean recipeNameExists(String recipeName, List<Recipe> recipes) {
        for (Recipe r : recipes) {
            if (r.getName().equals(recipeName) && !r.getName().equals(mRecipeName)) {
                return true;
            }
        }
        return false;
    }

    private void promptForRecipeEdit(Context context, List<Recipe> recipes, ValueCallback<Recipe> callback) {
        final View view = LayoutInflater.from(context).inflate(R.layout.recipe_name_dialog, null);
        final EditText nameET = view.findViewById(R.id.recipe_name_edittext);
        final CategoryEditText categoryET = view.findViewById(R.id.recipe_category_edittext);
        final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout);

        nameET.setText(mRecipeName);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("Edit recipe")
                .setPositiveButton("Save", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe recipe = new Recipe(nameET.getText().toString());
                recipe.addCategoriesIfNotExist(categoryET.getCategories());
                callback.onSuccess(recipe);
                dialog.dismiss();
            }
        });

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

        mCookBookViewModel.getAllRecipeCategories(new ValueCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> categories) {
                ArrayAdapter<CharSequence> categoryAdapter = new ArrayAdapter<CharSequence>(
                        view.getContext(),
                        android.R.layout.select_dialog_item,
                        categories.toArray(new String[0])
                );
                categoryET.setCustomAdapter(categoryAdapter);
                categoryET.setChippedCategories(mRecipeDetailViewModel.getCurrentRecipe().getCategories());
            }

            @Override
            public void onError(String error) {

            }
        });

    }

    private void clearAllChecks() {
        mRecipeDetailViewModel.clearAllChecks();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_OF_TABS = 2;
        private Fragment[] mFragments = new Fragment[NUMBER_OF_TABS];
        private String[] mFragmentTitles = {"Ingredients", "Steps"};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            if (mFragments[position] != null) {
                return mFragments[position];
            }
            switch (position) {
                case 0:
                    return new RecipeIngredientsFragment();
                case 1:
                    return new RecipeStepsFragment();
                default:
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mFragments[position] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_TABS;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles[position];
        }
    }
}
