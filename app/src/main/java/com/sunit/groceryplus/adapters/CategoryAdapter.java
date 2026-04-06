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
import com.sunit.groceryplus.utils.RealDeviceImageSystem;

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

        /** Binds name and loads category image with fallback to guaranteed icons. */
        public void bind(Category category) {
            categoryNameTv.setText(category.getCategoryName());
            
            // Try to load category image using RealDeviceImageSystem
            String imagePath = category.getImage();
            String categoryName = category.getCategoryName();
            
            Log.d("CATEGORY_DEBUG", "=== CATEGORY DEBUG ===");
            Log.d("CATEGORY_DEBUG", "Category Name: " + categoryName);
            Log.d("CATEGORY_DEBUG", "Image Path: " + imagePath);
            
            // Load image using RealDeviceImageSystem with fallback
            RealDeviceImageSystem.loadCategoryImage(context, categoryIcon, imagePath, categoryName);
        }
    }
}
