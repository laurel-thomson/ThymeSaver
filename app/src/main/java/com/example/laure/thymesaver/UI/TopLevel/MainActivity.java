package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.laure.thymesaver.Database.IPantryManagerRepository;
import com.example.laure.thymesaver.Database.Firebase.PantryManagerRepository;
import com.example.laure.thymesaver.Models.Follower;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.Callbacks.Callback;
import com.example.laure.thymesaver.UI.Settings.PantryManagerActivity;
import com.example.laure.thymesaver.ViewModels.PantryManagerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_INITIAL = 100;
    private static final int RC_SIGN_IN_SECONDARY = 101;
    private MenuItem mPreviousMenuItem;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;
    private ActionBar mActionBar;
    private PantryManagerViewModel mViewModel;
    private MaterialCardView mJoinRequestCardView;
    private List<Follower> mPantryRequests = new ArrayList<Follower>();
    private IPantryManagerRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpActionBar();
        mJoinRequestCardView = findViewById(R.id.join_request_card);

        //if the user is already logged in, we don't want to launch the sign in flow
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onSignInSuccess();
        }
        else {
            signIn();
        }
    }

    private void setUpActionBar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Meal Planner");
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
    }


    private void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN_INITIAL);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        onSignOut();
                    }
                });
    }

    private void onSignOut() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN_SECONDARY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_INITIAL) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                onSignInSuccess();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // We don't want to cancel sign in, so we'll just retry if they hit the
                // back button
                signIn();
            }
        }
        else if (requestCode == RC_SIGN_IN_SECONDARY) {
            //restart application
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private void onSignInSuccess() {
        mRepository = PantryManagerRepository.getInstance();
        mRepository.initializePreferredPantry(new Callback() {
            @Override
            public void onSuccess() {
                initializeActivity();
            }

            @Override
            public void onError(String err) {
                Snackbar snackbar = Snackbar
                        .make(findViewById(
                                R.id.pantry_manager_layout),
                                err,
                                Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    private void initializeActivity() {
        mFAB = findViewById(R.id.main_add_button);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddButtonFragment currentFragment = (AddButtonFragment) mAdapter.getItem(
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
                switch (position) {
                    case 0:
                        mActionBar.setTitle("Meal Planner");
                        break;
                    case 1:
                        mActionBar.setTitle("Cookbook");
                        break;
                    case 2:
                        mActionBar.setTitle("Pantry");
                        break;
                    case 3:
                        mActionBar.setTitle("Shopping List");
                        break;
                }
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

        listenForPantryRequests();
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
                        "Join request accepted!", Snackbar.LENGTH_SHORT).show();

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
                        "Join request declined.", Snackbar.LENGTH_SHORT).show();

                //get rid of listeners
                acceptButton.setOnClickListener(null);
                declineButton.setOnClickListener(null);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_level_menu, menu);
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
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
