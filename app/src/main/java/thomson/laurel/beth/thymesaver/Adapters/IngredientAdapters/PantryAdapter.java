package thomson.laurel.beth.thymesaver.Adapters.IngredientAdapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import thomson.laurel.beth.thymesaver.Models.BulkIngredientState;
import thomson.laurel.beth.thymesaver.Models.Ingredient;
import thomson.laurel.beth.thymesaver.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class PantryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private List<Ingredient> mIngredients = new ArrayList<>();
    private List<Ingredient> mFilteredIngredients = mIngredients;
    private IngredientListener mListener;
    private static final int INGREDIENT_TYPE = 1;
    private static final int HEADER_TYPE = 2;

    public PantryAdapter(
            Context context,
            IngredientListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        //Sort the ingredients
        Object[] ingArray = ingredients.toArray();
        Arrays.sort(ingArray, (o1, o2) -> ((Ingredient) o1).getName().compareTo(((Ingredient) o2).getName()));
        Collections.reverse(Arrays.asList(ingArray));

        mIngredients.clear();

        //generate category headers
        CharSequence[] categories = mContext.getResources().getStringArray(R.array.ingredient_categories);
        Ingredient[] headers = new Ingredient[categories.length];
        for (int i = 0; i < categories.length; i++) {
            headers[i] = new Ingredient("", categories[i].toString(), false);
        }

        //add the headers in to the list
        for (Ingredient i : headers) {
            mIngredients.add(i);
        }

        //add all the ingredients in under their headers
        for (Object object : ingArray) {
            Ingredient ingredient = (Ingredient) object;
            int position = 0;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getCategory().equals(ingredient.getCategory())) {
                    position = mIngredients.indexOf(headers[i]);
                    break;
                }
            }
            mIngredients.add(position+1, ingredient);
        }

        //remove any headers that don't have ingredients under them
        ListIterator<Ingredient> iterator = mIngredients.listIterator();
        while (iterator.hasNext()) {
            Ingredient ingredient = iterator.next();
            if (getItemViewType(ingredient) == HEADER_TYPE) {
                int nextIndex = iterator.nextIndex();
                if (nextIndex >= mIngredients.size() || getItemViewType(nextIndex) == HEADER_TYPE) {
                    iterator.remove();
                }
            }
        }
        mFilteredIngredients = mIngredients;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case INGREDIENT_TYPE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.ingredient_list_item, parent, false);
                return new IngredientViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.section_header, parent, false);
                return new SectionHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == INGREDIENT_TYPE) {
            final Ingredient ingredient = mFilteredIngredients.get(position);
            IngredientViewHolder holder = (IngredientViewHolder) viewHolder;
            holder.mNameTV.setText(ingredient.getName());

            //Bulk ingredients
            if (ingredient.isBulk()) {
                holder.mDecrementer.setVisibility(View.GONE);
                holder.mIncrementer.setVisibility(View.GONE);

                if (ingredient.getQuantity() == BulkIngredientState
                        .convertEnumToInt(BulkIngredientState.OUT_OF_STOCK)) {

                    holder.mQuantityTV.setText("Out of stock");
                    holder.mQuantityTV.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                } else if (ingredient.getQuantity() == BulkIngredientState
                        .convertEnumToInt(BulkIngredientState.RUNNING_LOW)) {

                    holder.mQuantityTV.setText("Running low");
                    holder.mQuantityTV.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                } else {
                    holder.mQuantityTV.setText("In stock");
                    holder.mQuantityTV.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
                }
            }

            //Non-bulk ingredients
            else {
                holder.mQuantityTV.setText(Integer.toString(ingredient.getQuantity()));
                holder.mDecrementer.setVisibility(View.VISIBLE);
                holder.mIncrementer.setVisibility(View.VISIBLE);
                holder.mQuantityTV.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            }
        }
        else {
            SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) viewHolder;
            final String category = mFilteredIngredients.get(position).getCategory();
            headerViewHolder.sectionTitle.setText(category);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mFilteredIngredients.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return INGREDIENT_TYPE;
        }
    }

    public int getItemViewType(Ingredient ingredient) {
        if (TextUtils.isEmpty(ingredient.getName())) {
            return HEADER_TYPE;
        } else {
            return INGREDIENT_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mFilteredIngredients.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredIngredients = mIngredients;
                } else {
                    List<Ingredient> filteredList = new ArrayList<>();
                    for (Ingredient row : mIngredients) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredIngredients = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredIngredients;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredIngredients = (ArrayList<Ingredient>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView mNameTV;
        TextView mQuantityTV;
        LinearLayout mDecrementer;
        LinearLayout mIncrementer;
        Button mDeleteButton;

        IngredientViewHolder(View view) {
            super(view);
            mCheckBox = view.findViewById(R.id.multiselect_item_checkbox);
            mNameTV = view.findViewById(R.id.multiselect_item_textview);
            mQuantityTV = view.findViewById(R.id.item_quantity_picker);
            mDecrementer = view.findViewById(R.id.decrement_quantity_layout);
            mIncrementer = view.findViewById(R.id.increment_quantity_layout);
            mDeleteButton = view.findViewById(R.id.ingredient_delete);

            mCheckBox.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient ingredient = mFilteredIngredients.get(getAdapterPosition());
                    mListener.onIngredientClicked(ingredient);
                }
            });

            mDecrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() == 0) return;
                    i.setQuantity(i.getQuantity() - 1);
                    mListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });

            mIncrementer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() < 0) return;
                    Ingredient i = mFilteredIngredients.get(getAdapterPosition());
                    if (i.getQuantity() > 100) return;
                    i.setQuantity(i.getQuantity() + 1);
                    mListener.onIngredientQuantityChanged(i, i.getQuantity());
                    notifyDataSetChanged();
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDeleteClicked(mFilteredIngredients.get(getAdapterPosition()));
                }
            });

            mQuantityTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Ingredient ingredient = mFilteredIngredients.get(getAdapterPosition());
                    if (!ingredient.isBulk()) return;

                    int newQuantity = BulkIngredientState.getNextStateAsInt(ingredient.getQuantity());
                    mListener.onIngredientQuantityChanged(ingredient, newQuantity);
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

    public interface IngredientListener {
        void onIngredientQuantityChanged(Ingredient ingredient, int quantity);

        void onDeleteClicked(Ingredient ingredient);

        void onIngredientClicked(Ingredient ingredient);
    }
}
