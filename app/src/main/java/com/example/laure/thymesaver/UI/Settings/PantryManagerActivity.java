package com.example.laure.thymesaver.UI.Settings;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.laure.thymesaver.Adapters.PantryListAdapter;
import com.example.laure.thymesaver.Models.Pantry;
import com.example.laure.thymesaver.R;
import com.example.laure.thymesaver.UI.TopLevel.MainActivity;
import com.example.laure.thymesaver.ViewModels.PantryManagerViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class PantryManagerActivity extends AppCompatActivity implements PantryListAdapter.PantryListListener {
    private PantryManagerViewModel mViewModel;
    private PantryListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private boolean pantryChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_manager);
        final ProgressBar progressBar = findViewById(R.id.pantry_manager_progress);

        mViewModel = ViewModelProviders.of(this).get(PantryManagerViewModel.class);
        mAdapter = new PantryListAdapter(this, this);
        mRecyclerView = findViewById(R.id.manage_pantry_rv);
        mRecyclerView.setVisibility(View.GONE);

        mViewModel.getPantries().observe(this, new Observer<List<Pantry>>() {
            @Override
            public void onChanged(@Nullable List<Pantry> pantries) {
                mAdapter.setPantryList(pantries, mViewModel.getPreferredPantryId());
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        setUpActionBar();

        FloatingActionButton fab = findViewById(R.id.pantry_manager_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestJoinPantry();
            }
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Manage Pantries");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_done);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void requestJoinPantry() {
        final View view = LayoutInflater.from(this).inflate(R.layout.join_pantry_dialog, null);
        final EditText emailET = view.findViewById(R.id.pantry_email_edittext);
        final TextInputLayout textInputLayout = view.findViewById(R.id.pantry_text_input_layout);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Request to Join a Pantry")
                .setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.requestJoinPantry(emailET.getText().toString());
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editable.toString();
                if (email.equals("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    return;
                }
                if (!isEmailValid(email)) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    textInputLayout.setError("Invalid email address");
                    return;
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                textInputLayout.setError(null);
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (pantryChanged) {
                    restartApplication();
                }
                else {
                    onBackPressed();
                }
                return true;
        }
        return false;
    }

    private void restartApplication() {
        //restart application
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onPreferredPantryChanged(String pantryId) {
        pantryChanged = true;
        mViewModel.updatePreferredPantry(pantryId);
        Snackbar snackbar = Snackbar
                .make(findViewById(
                        R.id.pantry_manager_layout),
                        "Preferred pantry updated.",
                        Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
