package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public AdminCategoryAdapter(Context context, List<Category> categories, OnCategoryActionListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_category, parent, false);
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

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTv;
        TextView categoryDescriptionTv;
        Button editCategoryBtn;
        Button deleteCategoryBtn;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);
            categoryDescriptionTv = itemView.findViewById(R.id.categoryDescriptionTv);
            editCategoryBtn = itemView.findViewById(R.id.editCategoryBtn);
            deleteCategoryBtn = itemView.findViewById(R.id.deleteCategoryBtn);

            editCategoryBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(categories.get(position));
                }
            });

            deleteCategoryBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(categories.get(position));
                }
            });
        }

        public void bind(Category category) {
            categoryNameTv.setText(category.getCategoryName());
            categoryDescriptionTv.setText(category.getCategoryDescription());
        }
    }
}
