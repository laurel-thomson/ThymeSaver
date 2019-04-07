package com.example.laure.thymesaver.UI.TopLevel;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.Settings.PantryManagerActivity;
import com.example.laure.thymesaver.ViewModels.PantryManagerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private MenuItem mPreviousMenuItem;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private FloatingActionButton mFAB;
    private ActionBar mActionBar;
    private PantryManagerViewModel mViewModel;
    private MaterialCardView mJoinRequestCardView;
    private List<PantryRequest> mPantryRequests = new ArrayList<PantryRequest>();
    private Repository mRepository;
    private SharedPreferences mSharedPreferences;
    private final String PREFERRED_PANTRY = "PREFFERED_PANTRY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Meal Planner");
        mJoinRequestCardView = findViewById(R.id.join_request_card);
        mSharedPreferences = getDefaultSharedPreferences(this);

        signIn();
    }


    private void signIn() {
        //if the user is already logged in, we don't want to launch the sign in flow
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onSignIn();
            return;
        }

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        signIn();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (isNewUser(user)) {
                    onNewUserCreated(user);
                }
                else {
                    setPreferredPantry();
                }
                onSignIn();
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
    }

    private void onNewUserCreated(FirebaseUser user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFERRED_PANTRY, user.getUid());
        editor.commit();
        mRepository = Repository.getInstance(user.getUid());
        mRepository.populateNewUserData();
    }

    private boolean isNewUser(FirebaseUser user) {
        return user.getMetadata().getLastSignInTimestamp() == user.getMetadata().getCreationTimestamp();
    }

    private void setPreferredPantry() {
        String preferredPantry = mSharedPreferences.getString(PREFERRED_PANTRY, null);
        mRepository = Repository.getInstance(preferredPantry);
    }

    private void onSignIn() {

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
        mViewModel.getPantryRequests().observe(this, new Observer<List<PantryRequest>>() {
            @Override
            public void onChanged(@Nullable List<PantryRequest> pantryRequests) {
                if (!pantryRequests.isEmpty()) {
                    PantryRequest firstRequest = pantryRequests.get(0);
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
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPantryRequests.isEmpty()) return;

                PantryRequest request = mPantryRequests.get(0);
                mViewModel.acceptJoinRequest(request);

                Snackbar.make(findViewById(R.id.activity_main_coordinator),
                        "Join request accepted!", Snackbar.LENGTH_SHORT).show();
            }
        });

        TextView declineButton = findViewById(R.id.decline_join_request);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPantryRequests.isEmpty()) return;

                PantryRequest request = mPantryRequests.get(0);
                mViewModel.declineJoinRequest(request);

                Snackbar.make(findViewById(R.id.activity_main_coordinator),
                        "Join request declined.", Snackbar.LENGTH_SHORT).show();
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
