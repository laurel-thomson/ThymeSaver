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

public class AddSubRecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Recipe> mPlannedRecipes = new ArrayList<>();
    private List<Recipe> mTotalRecipes;
    private List<Ingredient> mTotalIngredients;
    private HashMap<String, List<String>> mMissingIngredients = new HashMap<>();
    private final LayoutInflater mInflater;
    private Context mContext;

    public AddSubRecipesAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setTotalRecipes(List<Recipe> recipes) {
        mTotalRecipes = recipes;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.mealplan_grid_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
        Recipe recipe = mTotalRecipes.get(position);
        holder.mNameTV.setText(recipe.getName());
        if (recipe.getImageURL() != null) {
            Picasso.with(mContext).load(recipe.getImageURL()).fit().centerCrop().into(holder.mImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mTotalRecipes == null) return 0;
        return mTotalRecipes.size();
    }

    public String[] getRecipesArray() {
        String[] arr = new String[mPlannedRecipes.size()];
        for (int i = 0; i < mPlannedRecipes.size(); i++) {
            arr[i] = mPlannedRecipes.get(i).getName();
        }
        return arr;
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
}
