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

import com.example.laure.thymesaver.Firebase.Database.Repository;
import com.example.laure.thymesaver.Models.Ingredient;
import com.example.laure.thymesaver.Models.PantryRequest;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.Settings.PantryManagerActivity;
import com.example.laure.thymesaver.ViewModels.PantryManagerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Meal Planner");
        mJoinRequestCardView = findViewById(R.id.join_request_card);
        signIn();
    }


    private void signIn() {
        //if the user is already logged in, we don't want to launch the sign in flow
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            onSignInSuccess();
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
    }

    private void onSignInSuccess() {
        //Had to stick some database stuff in here because I couldn't figure out a nice way to pass initializeActivity as
        //a callback function when this code was in the repository :(
        mRepository = Repository.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = database.getReference("users/" + userId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pantryId;
                if (dataSnapshot.hasChild("preferredPantry")) {
                    pantryId = dataSnapshot.child("preferredPantry").getValue().toString();
                    mRepository.initializeDatabaseReferences(pantryId);
                }
                else {
                    //the default pantry id is just the user's own pantry (which is the user's id)
                    mRepository.initializeDatabaseReferences(userId);
                    //If the preferred pantry doesn't exist, then this is a new user & we need
                    //to initialize the pantry
                    mRepository.initializePantry();
                }
                initializeActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                        mFAB.show();
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
