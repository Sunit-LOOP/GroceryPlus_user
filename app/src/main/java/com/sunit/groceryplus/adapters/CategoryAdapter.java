package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.util.Log;
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

/** CategoryAdapter - Displays product categories on home screen with static icon fallbacks. */
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

    /** Updates the data source and refreshes UI. */
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

        /** Binds name and applies guaranteed icons based on category names for all devices. */
        public void bind(Category category) {
            categoryNameTv.setText(category.getCategoryName());
            
            // Debug logging for categories
            String name = category.getCategoryName();
            Log.d("CATEGORY_DEBUG", "=== CATEGORY DEBUG ===");
            Log.d("CATEGORY_DEBUG", "Category Name: " + name);
            Log.d("CATEGORY_DEBUG", "Image URL: " + category.getImageUrl());
            
            if (name == null) {
                categoryIcon.setImageResource(R.drawable.category_icon);
                return;
            }
            
            String lowerName = name.toLowerCase();
            
            // Guaranteed Category Mapping - Works on ALL devices
            if (lowerName.contains("dairy") || lowerName.contains("milk")) {
                categoryIcon.setImageResource(R.drawable.bottle_milk);
                Log.d("CATEGORY_DEBUG", "✓ Used dairy image: bottle_milk");
            } else if (lowerName.contains("fruit")) {
                categoryIcon.setImageResource(R.drawable.apple);
                Log.d("CATEGORY_DEBUG", "✓ Used fruit image: apple");
            } else if (lowerName.contains("vegetable")) {
                categoryIcon.setImageResource(R.drawable.green_vegetable);
                Log.d("CATEGORY_DEBUG", "✓ Used vegetable image: green_vegetable");
            } else if (lowerName.contains("beverage") || lowerName.contains("drink")) {
                categoryIcon.setImageResource(R.drawable.juice_bottle);
                Log.d("CATEGORY_DEBUG", "✓ Used beverage image: juice_bottle");
            } else if (lowerName.contains("bakery") || lowerName.contains("bread")) {
                categoryIcon.setImageResource(R.drawable.bread);
                Log.d("CATEGORY_DEBUG", "✓ Used bakery image: bread");
            } else if (lowerName.contains("staple") || lowerName.contains("rice") || lowerName.contains("oil")) {
                categoryIcon.setImageResource(R.drawable.rice_sack);
                Log.d("CATEGORY_DEBUG", "✓ Used staple image: rice_sack");
            } else {
                categoryIcon.setImageResource(R.drawable.category_icon);
                Log.d("CATEGORY_DEBUG", "✗ Used default icon: category_icon");
            }
        }
    }
}
