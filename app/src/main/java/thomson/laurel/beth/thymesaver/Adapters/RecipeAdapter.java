package thomson.laurel.beth.thymesaver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int RECIPE_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private RequestQueue mRequestQueue;

    public RecipeAdapter(
            Context context,
            RecipeListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.recipe_list_item, parent, false);
                return new RecipeViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    public void setRecipes(List<Recipe> recipes) {
        //Sort the recipes
        Object[] recipeArray = recipes.toArray();
        Arrays.sort(recipeArray, (o1, o2) -> ((Recipe) o1).getName().compareTo(((Recipe) o2).getName()));
        Collections.reverse(Arrays.asList(recipeArray));

        mRecipes.clear();

        //generate category headers
        CharSequence[] categories = mContext.getResources().getStringArray(R.array.recipe_categories);
        Recipe[] headers = new Recipe[categories.length];
        for (int i = 0; i < categories.length; i++) {
            headers[i] = new Recipe("", categories[i].toString());
        }

        //add the headers in to the list
        for (Recipe r : headers) {
            mRecipes.add(r);
        }

        //add all the ingredients in under their headers
        for (Object object : recipeArray) {
            Recipe recipe = (Recipe) object;
            int position = 0;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getCategory().equals(recipe.getCategory())) {
                    position = mRecipes.indexOf(headers[i]);
                    break;
                }
            }
            mRecipes.add(position+1, recipe);
        }

        //remove any headers that don't have ingredients under them
        ListIterator<Recipe> iterator = mRecipes.listIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (getItemViewType(recipe) == HEADER_TYPE) {
                int nextIndex = iterator.nextIndex();
                if (nextIndex >= mRecipes.size() || getItemViewType(nextIndex) == HEADER_TYPE) {
                    iterator.remove();
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == RECIPE_TYPE) {
            RecipeViewHolder holder = (RecipeViewHolder) viewHolder;
            holder.mTextView.setText(mRecipes.get(position).getName());

            String url = "https://api.unsplash.com/photos/random/?client_id=9c70e0edbba0b1946a5f28a7808f10e633132cb886ec5061d50563f8b6691a98&query="
                    + mRecipes.get(position).getName();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject jsonArray = (JSONObject) response.get("urls");
                                String imageURL = (String) jsonArray.get("small");
                                Picasso.with(mContext).load(imageURL).into(holder.mImageView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            mRequestQueue.add(jsonObjectRequest);
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mRecipes.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mRecipes.get(position).getName())) {
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

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recipe_name);
            mImageView = itemView.findViewById(R.id.recipe_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onRecipeSelected(mRecipes.get(getAdapterPosition()));
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

    public interface RecipeListener {
        void onRecipeSelected(Recipe recipe);

        void onDeleteClicked(Recipe recipe);
    }
}
