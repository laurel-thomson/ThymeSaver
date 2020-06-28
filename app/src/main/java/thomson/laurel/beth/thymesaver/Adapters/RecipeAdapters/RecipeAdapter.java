package thomson.laurel.beth.thymesaver.Adapters.RecipeAdapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import thomson.laurel.beth.thymesaver.Models.Recipe;
import thomson.laurel.beth.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RECIPE_ADAPTER";
    private List<Recipe> mRecipes = new ArrayList<>();
    private final LayoutInflater mInflater;
    private RecipeListener mListener;
    private Context mContext;

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
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
        Recipe recipe = mRecipes.get(position);
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
        return mRecipes.size();
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
                    mListener.onRecipeSelected(mRecipes.get(getAdapterPosition()), commonRecipeImage);
                }
            });

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(mRecipes.get(getAdapterPosition()));
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

    public interface RecipeListener {
        void onRecipeSelected(Recipe recipe, View recipeImage);

        void onDeleteClicked(Recipe recipe);
    }
}
