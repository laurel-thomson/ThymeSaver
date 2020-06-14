package thomson.laurel.beth.thymesaver.UI.TopLevel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import thomson.laurel.beth.thymesaver.Adapters.DragHelper;
import thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters.MealPlannerAdapter;
import thomson.laurel.beth.thymesaver.Models.MealPlan;
import thomson.laurel.beth.thymesaver.R;
import thomson.laurel.beth.thymesaver.UI.Callbacks.ValueCallback;
import thomson.laurel.beth.thymesaver.UI.RecipeDetail.RecipeDetailActivity;
import thomson.laurel.beth.thymesaver.ViewModels.MealPlannerViewModel;

import java.util.HashMap;
import java.util.List;


public class MealPlannerFragment extends Fragment implements MealPlannerAdapter.MealPlanListener {
    private RecyclerView mRecyclerView;
    private MealPlannerAdapter mAdapter;
    private MealPlannerViewModel mViewModel;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        return inflater.inflate(R.layout.recycler_view_layout, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.recycler_view_progress);
        mViewModel = ViewModelProviders.of(getActivity()).get(MealPlannerViewModel.class);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MealPlannerAdapter(getContext(), this);
        DragHelper swipeAndDragHelper = new DragHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(touchHelper);
        mRecyclerView.setAdapter(mAdapter);
        touchHelper.attachToRecyclerView(mRecyclerView);

        setObserver();
    }

    private void setObserver() {
        if (mViewModel.getMealPlans() == null) {
            //force the main activity to close and restart
            Intent intent = new Intent(getContext(), MainActivity.class);
            getActivity().finish();
            startActivity(intent);
            return;
        }

        mViewModel.getMealPlans().observe(this, new Observer<List<MealPlan>>() {
            @Override
            public void onChanged(@Nullable List<MealPlan> mealPlans) {
                mAdapter.setMealPlans(mealPlans);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onMealScheduleChanged(MealPlan mealPlan) {
        mViewModel.updateMealPlan(mealPlan);
    }

    @Override
    public void onMealClicked(MealPlan mealPlan, View recipeImage) {
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra(
                RecipeDetailActivity.CURRENT_RECIPE_NAME,
                mealPlan.getRecipeName());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View)recipeImage, "recipe_image");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onMealChecked(MealPlan mealPlan) {
        mProgressBar.setVisibility(View.VISIBLE);

        mViewModel.cookMealPlan(mealPlan, new ValueCallback<HashMap>() {
            @Override
            public void onSuccess(final HashMap oldIngredientQuantities) {
                mProgressBar.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar
                        .make(getView(), "Ingredients from " + mealPlan.getRecipeName() +
                                " removed from meal plan.", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mViewModel.undoCookMealPlan(mealPlan, oldIngredientQuantities);
                                Snackbar newSnackBar = Snackbar
                                        .make(
                                                getView(),
                                                "Ingredients from " + mealPlan.getRecipeName() + " added back to pantry.",
                                                Snackbar.LENGTH_SHORT);
                                newSnackBar.show();
                            }
                        });
                snackbar.show();
            }

            @Override
            public void onError(String err) {
                Snackbar snackbar = Snackbar.make(getView(), R.string.database_error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    @Override
    public void onMealDeleteClicked(final MealPlan mealPlan) {
        mViewModel.removeMealPlan(mealPlan);
        Snackbar snackbar = Snackbar
                .make(getView(), mealPlan.getRecipeName() +
                        " removed from meal plan.", Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.addMealPlan(mealPlan);
                        Snackbar newSnackBar = Snackbar
                                .make(getView(), "Meal plan restored.", Snackbar.LENGTH_SHORT);
                        newSnackBar.show();
                    }
                });

        snackbar.show();
    }

    @Override
    public void onAddButtonClicked(String scheduledDay) {
        Intent intent = new Intent(getActivity(), AddPlannedMealsActivity.class);
        intent.putExtra(AddPlannedMealsActivity.SCHEDULED_DAY, scheduledDay);
        startActivity(intent);
    }
}
