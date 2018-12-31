package com.example.laure.thymesaver.UI.TopLevel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.laure.thymesaver.R;

public class ShoppingListFragment extends AddButtonFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_shopping_list, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.shopping_list_progress);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    void launchAddItemActivity() {
        Toast.makeText(getActivity(), "Add Shopping List not implemented", Toast.LENGTH_SHORT).show();
    }
}
