package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Database.IPantryManagerRepository;
import thomson.laurel.beth.thymesaver.Models.Follower;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.LauncherActivity;
import thomson.laurel.beth.thymesaver.UI.Settings.PantryManagerActivity;
import thomson.laurel.beth.thymesaver.ViewModels.PantryManagerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MenuItem mPreviousMenuItem;
    private int mPreviousMenuPosition = 0;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private FloatingActionButton mFAB;
    private ActionBar mActionBar;
    private PantryManagerViewModel mViewModel;
    private MaterialCardView mJoinRequestCardView;
    private List<Follower> mPantryRequests = new ArrayList<Follower>();
    private IPantryManagerRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBar();
        mJoinRequestCardView = findViewById(R.id.join_request_card);
        initializeActivity();
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Meal Planner");
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
    }

    private void initializeActivity() {
        invalidateOptionsMenu();
        mFAB = findViewById(R.id.main_add_button);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThymesaverFragment currentFragment = (ThymesaverFragment) mViewPagerAdapter.getItem(
                        mViewPager.getCurrentItem());
                currentFragment.onFABClicked();
            }
        });

        mNavigationView = findViewById(R.id.navigation);
        mViewPager = findViewById(R.id.main_viewpager);

        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_meal_planner:
                                mViewPager.setCurrentItem(0);
                                break;
                            case R.id.action_cookbook:
                                mViewPager.setCurrentItem(1);
                                break;
                            case R.id.action_find:
                                mViewPager.setCurrentItem(2);
                                break;
                            case R.id.action_pantry:
                                mViewPager.setCurrentItem(3);
                                break;
                            case R.id.action_shopping_list:
                                mViewPager.setCurrentItem(4);
                                break;
                        }
                        return true;
                    }
                }
        );

        BottomNavigationView view = findViewById(R.id.navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        View iconView = menuView.getChildAt(2).findViewById(R.id.icon);
        iconView.setScaleY(1.5f);
        iconView.setScaleX(1.5f);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mPreviousMenuItem != null) {
                    mPreviousMenuItem.setChecked(false);
                    ((ThymesaverFragment) mViewPagerAdapter.getItem(mPreviousMenuPosition)).onFragmentLeft();
                } else {
                    mNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mNavigationView.getMenu().getItem(position).setChecked(true);
                mPreviousMenuPosition = position;
                mPreviousMenuItem = mNavigationView.getMenu().getItem(position);
                switch (position) {
                    case 0:
                        mActionBar.setTitle(R.string.meal_planner);
                        break;
                    case 1:
                        mActionBar.setTitle(R.string.cookbook);
                        break;
                    case 2:
                        mActionBar.setTitle(R.string.find_recipes);
                        break;
                    case 3:
                        mActionBar.setTitle(R.string.pantry);
                        break;
                    case 4:
                        mActionBar.setTitle(R.string.shopping_list);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        //the Meal Planner tab doesn't have a FAB
                        if (fragmentHasFAB(mViewPager.getCurrentItem())) {
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
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mViewPagerAdapter);

        //The activity starts on the Meal Planner tab, which doesn't have a FAB
        mFAB.hide();

        if (!isUserAnonymous()) {
            listenForPantryRequests();
        }
    }

    private boolean fragmentHasFAB(int fragmentIndex) {
        /* The Meal Planner fragment and Find Recipes fragment don't have a FAB */
        return fragmentIndex != 0 && fragmentIndex != 2;
    }

    private boolean isUserAnonymous() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null
                || FirebaseAuth.getInstance().getCurrentUser().getEmail() == null) {
            return true;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("");
    }

    private void listenForPantryRequests() {
        mViewModel = ViewModelProviders.of(this).get(PantryManagerViewModel.class);

        //if there are observers from a previous sign in, we want to get rid of them
        mViewModel.getPantryRequests().removeObservers(this);

        mViewModel.getPantryRequests().observe(this, new Observer<List<Follower>>() {
            @Override
            public void onChanged(@Nullable List<Follower> pantryRequests) {
                if (!pantryRequests.isEmpty()) {
                    Follower firstRequest = pantryRequests.get(0);
                    TextView requestTV = findViewById(R.id.request_text);
                    requestTV.setText("You have a new request to join your pantry!\nFrom user: " +
                        firstRequest.getUserName());
                    mJoinRequestCardView.setVisibility(View.VISIBLE);
                    mPantryRequests = pantryRequests;
                    setListenersForActionButtons();
                }
                else {
                    mPantryRequests.clear();
                    mJoinRequestCardView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setListenersForActionButtons() {
        TextView acceptButton = findViewById(R.id.accept_join_request);
        TextView declineButton = findViewById(R.id.decline_join_request);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPantryRequests.isEmpty()) return;

                Follower request = mPantryRequests.get(0);
                mViewModel.acceptJoinRequest(request);

                Snackbar.make(findViewById(R.id.activity_main_coordinator),
                        R.string.join_request_accepted, Snackbar.LENGTH_SHORT).show();

                //get rid of listeners
                acceptButton.setOnClickListener(null);
                declineButton.setOnClickListener(null);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPantryRequests.isEmpty()) return;

                Follower request = mPantryRequests.get(0);
                mViewModel.declineJoinRequest(request);

                Snackbar.make(findViewById(R.id.activity_main_coordinator),
                        R.string.join_request_declined, Snackbar.LENGTH_SHORT).show();

                //get rid of listeners
                acceptButton.setOnClickListener(null);
                declineButton.setOnClickListener(null);
            }
        });
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final int NUMBER_OF_TABS = 5;
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
                    return new FindRecipesFragment();
                case 3:
                    return new PantryFragment();
                case 4:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isUserAnonymous()) {
            inflater.inflate(R.menu.top_level_menu_anon, menu);
        }
        else {
            inflater.inflate(R.menu.top_level_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_pantries:
                Intent intent = new Intent(this, PantryManagerActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_out:
            case R.id.sign_in:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
