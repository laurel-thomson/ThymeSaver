package thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.R;

public class FixIngredientsAdapter extends RecyclerView.Adapter<FixIngredientsAdapter.MyViewHolder>{
    private List<Ingredient> mIngredients;
    private List<Ingredient> mRecipeIngredients = new ArrayList<>();
    private HashMap<String, RecipeQuantity> mRecipeQuantities;
    private Context mContext;
    private MyListener mListener;

    public void setRecipe(Recipe recipe) {
        mRecipeQuantities = recipe.getRecipeIngredients();
        if (mIngredients != null) {
            populateRecipeIngredients();
        }
    }

    public void setIngredients(List<Ingredient> ing) {
        mIngredients = ing;
        if (mRecipeQuantities != null) {
            populateRecipeIngredients();
        }
    }

    private void populateRecipeIngredients() {
        for (String name : mRecipeQuantities.keySet()) {
            for (Ingredient ing : mIngredients) {
                if (ing.getName().equals(name)) {
                    mRecipeIngredients.add(ing);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public FixIngredientsAdapter(Context context, MyListener listener) {
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public FixIngredientsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.checklist_item, parent, false);
        return new FixIngredientsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.mIngredientName.setText(mRecipeIngredients.get(i).getName());
    }


    @Override
    public int getItemCount() {
        if (mRecipeIngredients == null) return 0;

        return mRecipeIngredients.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mIngredientName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mIngredientName = itemView.findViewById(R.id.checklist_textview);
        }
    }

    public interface MyListener {

    }
}
