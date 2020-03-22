package thomson.laurel.beth.thymesaver.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.internal.StringUtil;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.Models.RecipeQuantity;
import thomson.laurel.beth.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class AddRecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recipe> mPlannedRecipes = new ArrayList<>();
    private List<Recipe> mTotalRecipes;
    private List<Ingredient> mTotalIngredients;
    private HashMap<String, List<String>> mMissingIngredients = new HashMap<>();
    private final LayoutInflater mInflater;
    private Context mContext;
    public static final int RECIPE_TYPE = 1;
    public static final int HEADER_TYPE = 2;

    public AddRecipesAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setTotalRecipes(List<Recipe> recipes) {
        mTotalRecipes = recipes;
        //add in ingredients from subrecipes
        HashMap<String, Recipe> recipeHashMap = new HashMap<>();
        for (Recipe recipe : mTotalRecipes) {
            recipeHashMap.put(recipe.getName(), recipe);
        }
        for (Recipe recipe : mTotalRecipes) {
            if (recipe.getSubRecipes() != null && recipe.getSubRecipes().size() > 0) {
                for (String subRecipeName : recipe.getSubRecipes()) {
                    Recipe subRecipe = recipeHashMap.get(subRecipeName);
                    for (String ing : subRecipe.getRecipeIngredients().keySet()) {
                        RecipeQuantity rq = subRecipe.getRecipeIngredients().get(ing);
                        if (recipe.getRecipeIngredients().containsKey(ing)) {
                            Double oldQuantity = recipe.getRecipeIngredients().get(ing).getRecipeQuantity();
                            Double newQuantity = rq.getRecipeQuantity();
                            recipe.getRecipeIngredients().get(ing).setRecipeQuantity(oldQuantity + newQuantity);
                        } else {
                            recipe.getRecipeIngredients().put(ing, rq);
                        }
                    }
                }
            }
        }
        if (mTotalIngredients != null) {
            sortRecipes();
        }
    }

    public void setTotalIngredients(List<Ingredient> ingredients) {
        mTotalIngredients = ingredients;
        if (mTotalRecipes != null) {
            sortRecipes();
        }
    }

    private int getMissingIngredients(Recipe recipe) {
        List<String> missingIngredients = new ArrayList<>();
        for (String ingName : recipe.getRecipeIngredients().keySet()) {
            for (Ingredient ing : mTotalIngredients) {
                if (ing.getName().equals(ingName)) {
                    if (ing.getQuantity() == 0) {
                        missingIngredients.add(ing.getName());
                        if (missingIngredients.size() == 3) {
                            mMissingIngredients.put(recipe.getName(), missingIngredients);
                            return missingIngredients.size();
                        }
                    }
                    break;
                }
            }
        }
        mMissingIngredients.put(recipe.getName(), missingIngredients);
        return missingIngredients.size();
    }

    private void sortRecipes() {
        //sort recipes alphabetically
        Object[] recipeArray = mTotalRecipes.toArray();
        Arrays.sort(recipeArray, (o1, o2) -> ((Recipe) o1).getName().compareTo(((Recipe) o2).getName()));
        Collections.reverse(Arrays.asList(recipeArray));
        mTotalRecipes.clear();

        //generate category headers
        Recipe[] headers = new Recipe[] {
                new Recipe("", "All ingredients in pantry"),
                new Recipe("", "Missing 1 ingredient"),
                new Recipe("", "Missing 2 ingredients"),
                new Recipe("", "Missing 3+ ingredients"),
        };

        //add the headers into the list
        for (Recipe r : headers) {
            mTotalRecipes.add(r);
        }

        //add the ingredients in under their headers
        for (Object object : recipeArray) {
            Recipe recipe = (Recipe) object;
            int position = mTotalRecipes.indexOf(headers[getMissingIngredients(recipe)]);
            mTotalRecipes.add(position+1, recipe);
        }

        //remove any headers that don't have ingredients under them
        ListIterator<Recipe> iterator = mTotalRecipes.listIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (getItemViewType(recipe) == HEADER_TYPE) {
                int nextIndex = iterator.nextIndex();
                if (nextIndex >= mTotalRecipes.size() || getItemViewType(nextIndex) == HEADER_TYPE) {
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    public List<Recipe> getPlannedRecipes() {
        return mPlannedRecipes;
    }

    public String[] getRecipesArray() {
        String[] arr = new String[mPlannedRecipes.size()];
        for (int i = 0; i < mPlannedRecipes.size(); i++) {
            arr[i] = mPlannedRecipes.get(i).getName();
        }
        return arr;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.mealplan_grid_item, parent, false);
                return new RecipeViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == RECIPE_TYPE) {
            final RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
            Recipe recipe = mTotalRecipes.get(position);
            holder.mNameTV.setText(recipe.getName());
            if (recipe.getImageURL() != null) {
                Picasso.with(mContext).load(recipe.getImageURL()).fit().centerCrop().into(holder.mImageView);
            }

            List<String> missingIngs = mMissingIngredients.get(recipe.getName());
            if (missingIngs.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < missingIngs.size(); i++) {
                    sb.append(missingIngs.get(i));
                    if (i == 2) {
                        sb.append("...");
                    }
                    else if (i != missingIngs.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
                holder.mMissingLabel.setVisibility(View.VISIBLE);
                holder.mMissingTV.setText(sb.toString());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
                }
            });
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mTotalRecipes.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mTotalRecipes.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    public int getItemViewType(Recipe recipe) {
        if (TextUtils.isEmpty(recipe.getName())) {
            return HEADER_TYPE;
        } else {
            return RECIPE_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (mTotalRecipes == null) return 0;
        return mTotalRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        ImageView mImageView;
        TextView mMissingTV;
        TextView mMissingLabel;

        public RecipeViewHolder(@NonNull View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.mealplan_checkbox);
            mNameTV = view.findViewById(R.id.mealplan_name);
            mImageView = itemView.findViewById(R.id.mealplan_image);
            mMissingTV = itemView.findViewById(R.id.missing_ings);
            mMissingLabel = itemView.findViewById(R.id.missing_label);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Recipe recipe = mTotalRecipes.get(getAdapterPosition());
                    if (compoundButton.isChecked()) {
                        mPlannedRecipes.add(recipe);
                    }
                    else {
                        mPlannedRecipes.remove(recipe);
                    }
                }
            });
        }
    }

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView sectionTitle;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.header_text);
        }
    }
}
