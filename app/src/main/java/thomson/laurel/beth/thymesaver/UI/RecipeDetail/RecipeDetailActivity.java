package thomson.laurel.beth.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import thomson.laurel.beth.thymesaver.Database.Firebase.StorageRepository;
import thomson.laurel.beth.thymesaver.Database.IStorageRepository;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeIngredients.RecipeIngredientsFragment;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeSteps.RecipeStepsFragment;
import thomson.laurel.beth.thymesaver.UI.TopLevel.AddButtonFragment;
import thomson.laurel.beth.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeDetailActivity extends AppCompatActivity{

    public static String CURRENT_RECIPE_NAME = "Current Recipe Name";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ViewPager mViewPager;
    private RecipeDetailViewModel mViewModel;
    private FloatingActionButton mFAB;
    private ViewPagerAdapter mAdapter;
    private IStorageRepository mStorageRepository;
    private String mRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mStorageRepository = StorageRepository.getInstance();
        mViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        mRecipeName= getIntent().getStringExtra(CURRENT_RECIPE_NAME);
        mViewModel.setCurrentRecipe(mRecipeName);
        mViewModel.getCurrentRecipe(new ValueCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe recipe) {
                if (recipe.getImageURL() != null) {
                    setRecipeImage(recipe.getImageURL());
                } else {
                    supportStartPostponedEnterTransition();
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
            AddButtonFragment currentFragment = (AddButtonFragment) mAdapter.getItem(
                    mViewPager.getCurrentItem());
            currentFragment.onFABClicked();
        });

        findViewById(R.id.camera_button).setOnClickListener(view -> dispatchTakePictureIntent());
    }

    private void setRecipeImage(String imageURL) {
        ImageView headerImage = findViewById(R.id.recipe_header_image);
        if (isNetworkAvailable()) {
            Picasso.with(this).load(imageURL).fit().centerCrop().into(headerImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        else {
            supportStartPostponedEnterTransition();
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
                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                mStorageRepository.uploadImage(photo, mRecipeName, new ValueCallback<String>() {
                    @Override
                    public void onSuccess(String downloadURL) {
                        Recipe recipe = mViewModel.getCurrentRecipe();
                        recipe.setImageURL(downloadURL);
                        mViewModel.updateRecipe();
                        setRecipeImage(downloadURL);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
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
            case R.id.action_renew:
                clearAllChecks();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private void clearAllChecks() {
        //todo : prompt user if sure
        mViewModel.clearAllChecks();
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
