package thomson.laurel.beth.thymesaver.UI.TopLevel;

import android.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters.ShoppingListAdapter;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.ModType;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.AddIngredients.AddIngredientFragment;
import thomson.laurel.beth.thymesaver.UI.AddIngredients.AddShoppingListItemFragment;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.ViewModels.ShoppingViewModel;

import java.util.HashMap;

public class ShoppingListFragment extends ThymesaverFragment
        implements ShoppingListAdapter.ShoppingListListener {

    private ShoppingViewModel mViewModel;
    private ShoppingListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mEmptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mViewModel = ViewModelProviders.of(this).get(ShoppingViewModel.class);
        mAdapter = new ShoppingListAdapter(getActivity(), this);
        mEmptyMessage = view.findViewById(R.id.empty_message);

        setObserver();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
    }

    private void setObserver() {
        if (mViewModel.getShoppingList() == null) {
            //force the main activity to close and restart
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().finish();
            startActivity(intent);
            return;
        }

        mViewModel.getShoppingList().observe(this, new Observer<HashMap<Ingredient, Integer>>() {
            @Override
            public void onChanged(@Nullable HashMap<Ingredient, Integer> shoppingList) {
                if (shoppingList.size() > 0) {
                    mEmptyMessage.setVisibility(View.GONE);
                }
                else {
                    mEmptyMessage.setVisibility(View.VISIBLE);
                }
                mAdapter.setIngredients(shoppingList);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFABClicked() {
        AddShoppingListItemFragment fragment = new AddShoppingListItemFragment();
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onFragmentLeft() {

    }

    @Override
    public void onIngredientQuantityChanged(Ingredient i, int quantity) {
        mViewModel.addShoppingModification(i.getName(), ModType.CHANGE, quantity);
    }

    @Override
    public void onIngredientCheckedOff(Ingredient i, int quantity) {
        mViewModel.tryFindIngredient(i, new ValueCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient ingredient) {
                addQuantityToPantry(i, quantity);
            }

            @Override
            public void onError(String error) {
                askAddIngredientToPantry(i, quantity);
            }
        });
    }

    private void askAddIngredientToPantry(final Ingredient ing, final int quantity) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_ingredient_pantry)
                .setMessage("Would you like to store this ingredient in the pantry?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addQuantityToPantry(ing, quantity);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mViewModel.deleteModifier(ing.getName());
                    }
                })
                .create();
        dialog.show();
    }

    private void addQuantityToPantry(Ingredient i, int quantity) {
        mViewModel.deleteModifier(i.getName());
        mViewModel.addQuantityToPantry(i, quantity);
    }

    @Override
    public void onDeleteClicked(Ingredient i, int quantity) {
        mViewModel.deleteShoppingListItem(i, quantity);
    }

    @Override
    public void onIngredientClicked(Ingredient ing) {
        Bundle bundle = new Bundle();
        bundle.putString(AddIngredientFragment.INGREDIENT_NAME, ing.getName());
        bundle.putString(AddIngredientFragment.INGREDIENT_CATEGORY, ing.getCategory());
        bundle.putBoolean(AddIngredientFragment.IS_BULK, ing.isBulk());

        AddIngredientFragment fragment = new AddIngredientFragment();
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_renew:
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.refresh_shopping_list)
                        .setMessage("Refreshing the shopping list will delete all " +
                                "manual modifications.")
                        .setPositiveButton("REFRESH", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mViewModel.refreshShoppingList();
                            }
                        })
                        .create();
                dialog.show();
                return true;
        }
        return false;
    }
}
