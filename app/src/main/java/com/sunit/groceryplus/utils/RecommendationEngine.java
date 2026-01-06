package com.sunit.groceryplus.utils;

import android.content.Context;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.models.Product;
import java.util.*;

import com.sunit.groceryplus.utils.CollaborativeFilteringEngine;

/** Orchestrates product recommendations by delegating to specialized filtering engines. */
public class RecommendationEngine {
    private DatabaseHelper dbHelper;
    private Context context;

    public RecommendationEngine(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Generates a list of personalized product recommendations for a specific user. */
    public List<Product> getRecommendations(int userId, int limit) {
        // Use the new advanced collaborative filtering engine
        CollaborativeFilteringEngine cfEngine = new CollaborativeFilteringEngine(context);
        return cfEngine.getRecommendations(userId, limit);
    }
}
