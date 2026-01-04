package com.sunit.groceryplus.utils;

import android.content.Context;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.models.Product;
import java.util.*;

import com.sunit.groceryplus.utils.CollaborativeFilteringEngine;

/**
 * RecommendationEngine - Provide product recommendations.
 * 
 * This class serves as the entry point for generating personalized product
 * recommendations. It delegates the complex logic to the 
 * {@link CollaborativeFilteringEngine} for a hybrid filtering approach
 * (Content-Based + Collaborative).
 */
public class RecommendationEngine {
    private DatabaseHelper dbHelper;
    private Context context;

    public RecommendationEngine(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Gets personalized recommendations using advanced collaborative filtering.
     * 
     * This method delegates to the new CollaborativeFilteringEngine
     * for comprehensive collaborative filtering implementation.
     * 
     * @param userId Target user ID for recommendations
     * @param limit Maximum number of recommendations to return
     * @return List of recommended products sorted by relevance
     */
    public List<Product> getRecommendations(int userId, int limit) {
        // Use the new advanced collaborative filtering engine
        CollaborativeFilteringEngine cfEngine = new CollaborativeFilteringEngine(context);
        return cfEngine.getRecommendations(userId, limit);
    }
}
