package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

public class RecipeDetailActivity extends AppCompatActivity{

    public static String CURRENT_RECIPE_NAME = "Current Recipe Name";
    private ViewPager mViewPager;
    private RecipeDetailViewModel mViewModel;
    private FloatingActionButton mFAB;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        setUpActionBar();

        //prevents the bottom window dialog from pushing the FAB up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(CURRENT_RECIPE_NAME);
        mViewModel.setCurrentRecipe(recipeName);

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
                        //the Recipe Info tab doesn't have a FAB
                        if (mViewPager.getCurrentItem() != 0) {
                            mFAB.show();
                        }
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
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeDetailFragment currentFragment = (RecipeDetailFragment) mAdapter.getItem(
                        mViewPager.getCurrentItem());
                currentFragment.launchAddItemActivity();
            }
        });
        //The activity starts on the Recipe Info tab, which doesn't have a FAB
        mFAB.hide();
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Recipe Details");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_OF_TABS = 3;
        private Fragment[] mFragments = new Fragment[NUMBER_OF_TABS];
        private String[] mFragmentTitles = {"Info", "Ingredients", "Steps"};

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
                    return new AboutRecipeFragment();
                case 1:
                    return new RecipeIngredientsFragment();
                case 2:
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
