package com.example.laure.thymesaver.UI.TopLevel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.laure.thymesaver.R;

public class ShoppingListFragment extends AddableFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_shopping_list, viewGroup, false);
    }

    @Override
    void launchAddItemActivity() {
        Toast.makeText(getActivity(), "Add Shopping List not implemented", Toast.LENGTH_SHORT).show();
    }
}
