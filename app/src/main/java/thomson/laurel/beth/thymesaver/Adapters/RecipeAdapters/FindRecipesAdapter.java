package thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;

public class FindRecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Recipe> mRecipes;
    private List<Recipe> mImportedRecipes;
    private List<Recipe> mCookbook;
    private List<Boolean> mFavoriteStates = new ArrayList<>();
    private FindRecipesListener mListener;

    public FindRecipesAdapter(Context context, FindRecipesListener listener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    public void setRecipes(List<Recipe> recipes) {
        mImportedRecipes = recipes;
        if (mCookbook != null) {
            organizeRecipes();
        }
    }

    public void setCookbook(List<Recipe> recipes) {
        mCookbook = recipes;
        if (mImportedRecipes != null) {
            organizeRecipes();
        }
    }

    private void organizeRecipes() {
        List<Recipe> newList = new ArrayList<>();
        mFavoriteStates.clear();
        for (int i = 0; i < mImportedRecipes.size(); i++) {
            Recipe recipe = mImportedRecipes.get(i);
            Recipe cookbookRecipe = tryGetRecipeInCookbook(recipe);
            if (cookbookRecipe != null) {
                newList.add(cookbookRecipe);
                mFavoriteStates.add(true);
            } else {
                newList.add(recipe);
                mFavoriteStates.add(false);
            }
        }
        mRecipes = newList;
        notifyDataSetChanged();
    }

    private Recipe tryGetRecipeInCookbook(Recipe recipe) {
        for (Recipe r : mCookbook) {
            if (r.getSourceURL() != null && recipe.getSourceURL() != null && r.getSourceURL().equals(recipe.getSourceURL())) {
                return r;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipe_grid_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder viewHolder = (RecipeViewHolder) holder;
        Recipe recipe = mImportedRecipes.get(position);
        viewHolder.mNameTV.setText(recipe.getName());
        if (recipe.getImageURL() != null) {
            Picasso.with(mContext).load(recipe.getImageURL()).fit().centerCrop().into(viewHolder.mImageView);
        } else {
            viewHolder.mImageView.setImageResource(R.mipmap.ic_launcher_foreground);
        }
        Boolean isFavorite = mFavoriteStates.get(position);
        if (isFavorite) {
            viewHolder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
        }

        viewHolder.itemView.setOnClickListener(view -> mListener.onFavoriteClicked(recipe));
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTV;
        ImageView mImageView;
        ImageView mFavoriteIcon;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTV = itemView.findViewById(R.id.recipe_name);
            mImageView = itemView.findViewById(R.id.recipe_image);
            mFavoriteIcon = itemView.findViewById(R.id.recipe_favorite);
        }
    }

    public interface FindRecipesListener {
        void onFavoriteClicked(Recipe recipe);

        void onUnfavoriteClicked(Recipe recipe);
    }

}
