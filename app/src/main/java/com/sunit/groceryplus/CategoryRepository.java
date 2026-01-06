package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.Category;

import java.util.ArrayList;
import java.util.List;

/** Repository for managing product categories in the database. */
public class CategoryRepository {
    // Infrastructure
    private static final String TAG = "CategoryRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Adds a new category to the database. */
    public boolean addCategory(String categoryName, String categoryDescription, String image) {
        try {
            long result = dbHelper.addCategory(categoryName, categoryDescription, image);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding category", e);
            return false;
        }
    }

    /** Retrieves all product categories from the database. */
    public List<Category> getAllCategories() {
        try {
            return dbHelper.getAllCategories();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all categories", e);
            return new ArrayList<>();
        }
    }

    /** Retrieves a specific category by its ID. */
    public Category getCategoryById(int categoryId) {
        try {
            return dbHelper.getCategoryById(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting category by ID", e);
            return null;
        }
    }

    /** Updates an existing category's information. */
    public boolean updateCategory(int categoryId, String categoryName, String categoryDescription, String image) {
        try {
            return dbHelper.updateCategory(categoryId, categoryName, categoryDescription, image);
        } catch (Exception e) {
            Log.e(TAG, "Error updating category", e);
            return false;
        }
    }

    /** Deletes a category by its ID. */
    public boolean deleteCategory(int categoryId) {
        try {
            return dbHelper.deleteCategory(categoryId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting category", e);
            return false;
        }
    }
}