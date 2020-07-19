package thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = "RECIPE_ADAPTER";
    private List<Recipe> mRecipes = new ArrayList<>();
    private List<Recipe> mFilteredRecipes = mRecipes;
    private final LayoutInflater mInflater;
    private RecipeListener mListener;
    private Context mContext;
    private boolean mIsFiltered;

    public RecipeAdapter(
            Context context,
            RecipeListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    public void setRecipes(List<Recipe> recipes) {
        if (mIsFiltered) { return; }
        mRecipes = recipes;
        mFilteredRecipes = mRecipes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
        Recipe recipe = mFilteredRecipes.get(position);
        holder.mNameTV.setText(recipe.getName());
        if (recipe.getImageURL() != null) {
            Picasso.with(mContext).load(recipe.getImageURL()).fit().centerCrop().into(holder.mImageView);
        } else {
            holder.mImageView.setImageResource(R.mipmap.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mFilteredRecipes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mIsFiltered = false;
                    mFilteredRecipes = mRecipes;
                } else {
                    mIsFiltered = true;
                    List<Recipe> filteredList = new ArrayList<>();
                    for (Recipe row : mRecipes) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredRecipes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredRecipes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredRecipes = (ArrayList<Recipe>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTV;
        private ImageView mImageView;
        private TextView mDelete;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTV = itemView.findViewById(R.id.recipe_name);
            mImageView = itemView.findViewById(R.id.recipe_image);
            mDelete = itemView.findViewById(R.id.recipe_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View commonRecipeImage = itemView.findViewById(R.id.recipe_image);
                    mListener.onRecipeSelected(mFilteredRecipes.get(getAdapterPosition()), commonRecipeImage);
                }
            });

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(mFilteredRecipes.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }

    public void clearFilter() {
        mFilteredRecipes = mRecipes;
        mIsFiltered = false;
        notifyDataSetChanged();
    }

    public interface RecipeListener {
        void onRecipeSelected(Recipe recipe, View recipeImage);

        void onDeleteClicked(Recipe recipe);
    }
}
