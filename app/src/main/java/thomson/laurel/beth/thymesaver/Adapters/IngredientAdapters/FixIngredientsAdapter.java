package thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.R;

public class FixIngredientsAdapter extends RecyclerView.Adapter<FixIngredientsAdapter.MyViewHolder>{
    private List<Ingredient> mIngredients;
    private HashMap<String, Ingredient> mIngredientStrings = new HashMap<>();
    private List<String> mRecipeIngredients = new ArrayList<>();
    private HashMap<String, RecipeQuantity> mRecipeQuantities;
    private Context mContext;
    private MyListener mListener;

    public void setRecipe(Recipe recipe) {
        mRecipeQuantities = recipe.getRecipeIngredients();
        for (String name : recipe.getRecipeIngredients().keySet()) {
            mRecipeIngredients.add(name);
        }
    }

    public void setIngredients(List<Ingredient> ing) {
        mIngredients = ing;
        for (Ingredient i : ing) {
            mIngredientStrings.put(i.getName(),i);
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
        View v = (View) LayoutInflater.from(mContext)
                .inflate(R.layout.fix_ingredient_item, parent, false);
        return new FixIngredientsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String ingName = mRecipeIngredients.get(i);
        RecipeQuantity quantity = mRecipeQuantities.get(ingName);
        myViewHolder.mNameTV.setText(ingName);
        myViewHolder.mQuantityET.setText(Double.toString(quantity.getRecipeQuantity()));
        myViewHolder.mUnitET.setText(quantity.getUnit());

        setSuggestedName(myViewHolder, ingName);

    }

    private void setSuggestedName(MyViewHolder viewHolder, String ingName) {
        ExtractedResult result = FuzzySearch.extractOne(ingName, mIngredientStrings.keySet());
        if (result.getScore() >= 70) {
            viewHolder.mNameET.setText(result.getString());
            viewHolder.mNameET.setTextColor(Color.RED);
        }
        else {
            viewHolder.mNameET.setText(ingName);
        }
    }


    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;

        return mRecipeIngredients.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTV;
        private EditText mQuantityET;
        private EditText mUnitET;
        private EditText mNameET;
        private EditText mCategoryET;
        private Switch mIsBulkSwitch;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTV = itemView.findViewById(R.id.name_textview);
            mQuantityET = itemView.findViewById(R.id.quantity_edittext);
            mUnitET = itemView.findViewById(R.id.unit_edittext);
            mNameET = itemView.findViewById(R.id.name_edittext);
            mCategoryET = itemView.findViewById(R.id.category_edittext);
            mIsBulkSwitch = itemView.findViewById(R.id.is_bulk_switch);
        }
    }

    public interface MyListener {

    }
}
