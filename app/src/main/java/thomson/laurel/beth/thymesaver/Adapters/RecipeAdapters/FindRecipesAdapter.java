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

import java.util.List;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;

public class FindRecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Recipe> mRecipes;

    public FindRecipesAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecipeViewHolder viewHolder = (RecipeViewHolder) holder;
        Recipe recipe = mRecipes.get(position);
        viewHolder.mNameTV.setText(recipe.getName());
        if (recipe.getImageURL() != null) {
            Picasso.with(mContext).load(recipe.getImageURL()).fit().centerCrop().into(viewHolder.mImageView);
        } else {
            viewHolder.mImageView.setImageResource(R.mipmap.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView mNameTV;
        ImageView mImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTV = itemView.findViewById(R.id.recipe_name);
            mImageView = itemView.findViewById(R.id.recipe_image);
        }
    }

}
