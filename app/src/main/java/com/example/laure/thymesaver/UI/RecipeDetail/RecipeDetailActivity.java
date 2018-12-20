package com.example.laure.thymesaver.UI.RecipeDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.TopLevel.CookbookFragment;
import com.example.laure.thymesaver.UI.TopLevel.PantryFragment;
import com.example.laure.thymesaver.UI.TopLevel.ShoppingListFragment;
import com.example.laure.thymesaver.UI.TopLevel.TopLevelFragment;
import com.example.laure.thymesaver.ViewModels.RecipeDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity{

    public static String CURRENT_RECIPE_NAME = "Current Recipe Name";
    private ViewPager mViewPager;
    private RecipeDetailViewModel mViewModel;
    private FloatingActionButton mFAB;
    private ViewPagerAdapter mAdapter;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        mActionBar = getSupportActionBar();
        mActionBar.setElevation(0);

        mViewModel = ViewModelProviders.of(this).get(RecipeDetailViewModel.class);
        String recipeName = getIntent().getStringExtra(CURRENT_RECIPE_NAME);
        mViewModel.setCurrentRecipe(recipeName);

        mViewPager = findViewById(R.id.pager);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new AboutRecipeFragment(), "Info");
        mAdapter.addFragment(new RecipeIngredientsFragment(), "Ingredients");
        mAdapter.addFragment(new RecipeStepsFragment(), "Steps");
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
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeDetailFragment currentFragment = (RecipeDetailFragment) mAdapter.getItem(
                        mViewPager.getCurrentItem());
                currentFragment.launchAddItemActivity();
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
