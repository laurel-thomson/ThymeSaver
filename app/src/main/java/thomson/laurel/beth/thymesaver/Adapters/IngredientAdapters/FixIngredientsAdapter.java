package thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
    private List<Ingredient> mTotalIngredients;
    private HashMap<String, Ingredient> mTotalIngredientStrings = new HashMap<>();
    private List<RecipeIngredient> mRecipeIngredients = new ArrayList<>();
    private Recipe mRecipe;
    private Context mContext;

    public void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        mTotalIngredients = ingredients;
        for (Ingredient ing : ingredients) {
            mTotalIngredientStrings.put(ing.getName(), ing);
        }
        populateRecipeIngredients();
        notifyDataSetChanged();
    }


    public List<Ingredient> getFixedIngredients() {
        List<Ingredient> fixedIngredients = new ArrayList<>();
        for (RecipeIngredient ri : mRecipeIngredients) {
            if (!mTotalIngredientStrings.containsKey(ri.fixedName)) {
                Ingredient ing = new Ingredient();
                ing.setName(ri.fixedName);
                ing.setCategory(ri.category);
                ing.setBulk(ri.isBulk);
                fixedIngredients.add(ing);
            }
        }
        return fixedIngredients;
    }

    public HashMap<String, RecipeQuantity> getRecipeIngredients() {
        HashMap<String, RecipeQuantity> recipeIngredients = new HashMap<>();
        for (RecipeIngredient ri : mRecipeIngredients) {
            recipeIngredients.put(ri.fixedName,
                    new RecipeQuantity(ri.unit, ri.quantity));
        }
        return recipeIngredients;
    }

    public FixIngredientsAdapter(Context context) {
        mContext = context;
    }


    private void populateRecipeIngredients() {
        for (String ingName : mRecipe.getRecipeIngredients().keySet()) {
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.oldName = ingName;
            recipeIngredient.quantity = mRecipe.getRecipeIngredients().get(ingName).getRecipeQuantity();
            recipeIngredient.unit = mRecipe.getRecipeIngredients().get(ingName).getUnit();
            setSuggestions(recipeIngredient);
            mRecipeIngredients.add(recipeIngredient);
        }
    }

    private void setSuggestions(RecipeIngredient recipeIngredient) {
        ExtractedResult result = FuzzySearch.extractOne(recipeIngredient.oldName, mTotalIngredientStrings.keySet());
        if (result.getScore() >= 80) {
            Ingredient suggestedIngredient = mTotalIngredientStrings.get(result.getString());
            recipeIngredient.fixedName = suggestedIngredient.getName();
            recipeIngredient.category = suggestedIngredient.getCategory();
            recipeIngredient.isBulk = suggestedIngredient.isBulk();
        }
        else {
            recipeIngredient.fixedName = recipeIngredient.oldName;
            recipeIngredient.category = "Misc";
            recipeIngredient.isBulk = true;
        }
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
        RecipeIngredient recipeIngredient = mRecipeIngredients.get(i);
        myViewHolder.mNameTV.setText(recipeIngredient.oldName);
        myViewHolder.mQuantityET.setText(Double.toString(recipeIngredient.quantity));
        myViewHolder.mUnitET.setText(recipeIngredient.unit);
        myViewHolder.mIsBulkSwitch.setChecked(recipeIngredient.isBulk);
        myViewHolder.mNameET.setText(recipeIngredient.fixedName);
        myViewHolder.mCategoryET.setText(recipeIngredient.category);

        myViewHolder.mNameET.addTextChangedListener(new TextChangedListener<EditText>(myViewHolder.mNameET) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newName = myViewHolder.mNameET.getText().toString();
                recipeIngredient.fixedName = newName;
            }
        });

        myViewHolder.mUnitET.addTextChangedListener(new TextChangedListener<EditText>(myViewHolder.mUnitET) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newUnit = myViewHolder.mUnitET.getText().toString();
                recipeIngredient.unit = newUnit;
            }
        });

        myViewHolder.mCategoryET.addTextChangedListener(new TextChangedListener<EditText>(myViewHolder.mCategoryET) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newCategory = myViewHolder.mCategoryET.getText().toString();
                recipeIngredient.category = newCategory;
            }
        });

        myViewHolder.mQuantityET.addTextChangedListener(new TextChangedListener<EditText>(myViewHolder.mQuantityET) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                Double newQuantity = Double.parseDouble(myViewHolder.mQuantityET.getText().toString());
                recipeIngredient.quantity = newQuantity;
            }
        });

        myViewHolder.mIsBulkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                recipeIngredient.isBulk = b;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mTotalIngredients == null) return 0;

        return mRecipeIngredients.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTV;
        public EditText mQuantityET;
        public EditText mUnitET;
        public EditText mNameET;
        public EditText mCategoryET;
        public Switch mIsBulkSwitch;

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

    private class RecipeIngredient {
        private String oldName;
        private String fixedName;
        private String category;
        private Boolean isBulk;
        private Double quantity;
        private String unit;
    }

    private abstract class TextChangedListener<T> implements TextWatcher {
        private T target;

        public TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }
}
