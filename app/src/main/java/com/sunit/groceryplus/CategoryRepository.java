package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private DatabaseHelper dbHelper;

    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add a new category
     */
    public boolean addCategory(String categoryName, String categoryDescription) {
        try {
            long result = dbHelper.addCategory(categoryName, categoryDescription);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding category", e);
            return false;
        }
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        try {
            return dbHelper.getAllCategories();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all categories", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(int categoryId) {
        try {
            return dbHelper.getCategoryById(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting category by ID", e);
            return null;
        }
    }

    /**
     * Update category
     */
    public boolean updateCategory(int categoryId, String categoryName, String categoryDescription) {
        try {
            return dbHelper.updateCategory(categoryId, categoryName, categoryDescription);
        } catch (Exception e) {
            Log.e(TAG, "Error updating category", e);
            return false;
        }
    }

    /**
     * Delete category
     */
    public boolean deleteCategory(int categoryId) {
        try {
            return dbHelper.deleteCategory(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category", e);
            return false;
        }
    }
}