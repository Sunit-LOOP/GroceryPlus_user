package com.sunit.groceryplus.repositories;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

import com.sunit.groceryplus.models.Category;

/**
 * Stub implementation for CategoryRepository in repositories package
 * This is a placeholder to resolve compilation errors
 */
public class CategoryRepository {
    
    private Context context;
    private com.sunit.groceryplus.CategoryRepository realRepository;
    
    public CategoryRepository(Context context) {
        this.context = context;
        this.realRepository = new com.sunit.groceryplus.CategoryRepository(context);
    }
    
    public List<Category> getAllCategories() {
        return realRepository.getAllCategories();
    }
}
