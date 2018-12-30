package com.example.laure.thymesaver.UI.TopLevel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.laure.thymesaver.R;

public class MainActivity extends AppCompatActivity {
    
    private MenuItem mPreviousMenuItem;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFAB = findViewById(R.id.main_add_button);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddButtonFragment currentFragment = (AddButtonFragment) mAdapter.getItem(
                        mViewPager.getCurrentItem());
                currentFragment.launchAddItemActivity();
            }
        });

        mNavigationView = findViewById(R.id.navigation);
        mViewPager = findViewById(R.id.main_viewpager);
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch(menuItem.getItemId())
                        {
                            case R.id.action_meal_planner:
                                mViewPager.setCurrentItem(0);
                                break;
                            case R.id.action_cookbook:
                                mViewPager.setCurrentItem(1);
                                break;
                            case R.id.action_pantry:
                                mViewPager.setCurrentItem(2);
                                break;
                            case R.id.action_shopping_list:
                                mViewPager.setCurrentItem(3);
                                break;
                        }
                        return true;
                    }
                }
        );
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mPreviousMenuItem != null) {
                    mPreviousMenuItem.setChecked(false);
                } else {
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mNavigationView.getMenu().getItem(position).setChecked(true);
                mPreviousMenuItem = mNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        //the Meal Planner tab doesn't have a FAB
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
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mAdapter);

        //The activity starts on the Meal Planner tab, which doesn't have a FAB
        mFAB.hide();
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_OF_TABS = 4;
        private Fragment[] mFragments = new Fragment[NUMBER_OF_TABS];

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            if (mFragments[position] != null)
                return mFragments[position];

            switch (position) {
                case 0:
                    return new MealPlannerFragment();
                case 1:
                    return new CookbookFragment();
                case 2:
                    return new PantryFragment();
                case 3:
                    return new ShoppingListFragment();
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
    }
}
