package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Category;

import java.util.List;

/** CategoryAdapter - Displays product categories on the home screen with static icon fallbacks. */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnCategoryClickListener listener;

        /** Interface to handle category selection. */
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    /** Constructor. */
    public CategoryAdapter(Context context, List<Category> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Updated to use modern layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_category_card_modern, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    /** Updates the data source and refreshes the UI. */
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    /** ViewHolder for Category item. */
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView categoryNameTv;
        ImageView categoryIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTv = itemView.findViewById(R.id.categoryName);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }

        /** Binds name and applies static icons based on category strings. */
        public void bind(Category category) {
            categoryNameTv.setText(category.getCategoryName());
            
            // Hardcoded Logic to set icons based on known category names
            // This is a fallback/enhancement when dynamic images are not fully implemented for categories
            String name = category.getCategoryName();
            if (name == null) {
                categoryIcon.setImageResource(R.drawable.category_icon);
                return;
            }

            if (name.equalsIgnoreCase("Vegetables")) {
                categoryIcon.setImageResource(R.drawable.green_vegetable);
            } else if (name.equalsIgnoreCase("Fruits")) {
                categoryIcon.setImageResource(R.drawable.apple); // Updated to use Apple image
            } else if (name.equalsIgnoreCase("Dairy") || name.equalsIgnoreCase("Milk")) {
                categoryIcon.setImageResource(R.drawable.bottle_milk);
            } else if (name.equalsIgnoreCase("Beverages") || name.equalsIgnoreCase("Drinks")) {
                 categoryIcon.setImageResource(R.drawable.juice_bottle); // Updated to use Juice image
            } else if (name.equalsIgnoreCase("Bakery") || name.equalsIgnoreCase("Bread")) {
                categoryIcon.setImageResource(R.drawable.bread); // Updated to use Bread image
            } else if (name.equalsIgnoreCase("Staples") || name.contains("Rice") || name.contains("Oil")) {
                categoryIcon.setImageResource(R.drawable.rice_sack); // Updated to use Rice image
            } else {
                 categoryIcon.setImageResource(R.drawable.category_icon);
            }
        }
    }
}
