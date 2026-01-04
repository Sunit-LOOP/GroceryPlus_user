package com.sunit.groceryplus.utils;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * CollaborativeFilteringTest - Test class for collaborative filtering functionality
 * 
 * This class provides methods to test and validate the collaborative filtering
 * implementation to ensure it's working correctly.
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class CollaborativeFilteringTest {
    
    private static final String TAG = "CollaborativeFilteringTest";
    
    /**
     * Test collaborative filtering with sample data
     * 
     * @param context Application context
     */
    public static void testCollaborativeFiltering(Context context) {
        try {
            Log.d(TAG, "=== Testing Collaborative Filtering ===");
            
            CollaborativeFilteringEngine engine = new CollaborativeFilteringEngine(context);
            
            // Test 1: Basic recommendation generation
            testBasicRecommendations(engine);
            
            // Test 2: Similarity calculations
            testSimilarityCalculations(engine);
            
            // Test 3: Cold start handling
            testColdStartHandling(engine);
            
            // Test 4: Performance with caching
            testPerformanceCaching(engine);
            
            Log.d(TAG, "=== Collaborative Filtering Test Complete ===");
            
        } catch (Exception e) {
            Log.e(TAG, "Error testing collaborative filtering", e);
        }
    }
    
    /**
     * Test basic recommendation generation
     */
    private static void testBasicRecommendations(CollaborativeFilteringEngine engine) {
        Log.d(TAG, "Testing basic recommendations...");
        
        // Test with sample user IDs
        int[] testUsers = {1, 2, 3, 4, 5};
        
        for (int userId : testUsers) {
            List<Product> recommendations = engine.getRecommendations(userId, 5);
            Log.d(TAG, "User " + userId + " got " + recommendations.size() + " recommendations");
            
            // Verify recommendations are not null and have products
            if (recommendations != null && !recommendations.isEmpty()) {
                for (Product product : recommendations) {
                    if (product != null) {
                        Log.d(TAG, "  - Recommended product: " + product.getProductName() + " (ID: " + product.getProductId() + ")");
                    }
                }
            }
        }
    }
    
    /**
     * Test similarity calculations
     */
    private static void testSimilarityCalculations(CollaborativeFilteringEngine engine) {
        Log.d(TAG, "Testing similarity calculations...");
        
        // Test Jaccard similarity (Expects Sets)
        java.util.Set<Integer> set1 = new java.util.HashSet<>(java.util.Arrays.asList(1, 2, 3));
        java.util.Set<Integer> set2 = new java.util.HashSet<>(java.util.Arrays.asList(2, 3, 4));
        
        double jaccard = engine.calculateJaccardSimilarity(set1, set2);
        Log.d(TAG, "Jaccard similarity (user1, user2): " + jaccard);
        
        // Test cosine similarity (Expects Maps: ItemID -> Rating)
        java.util.Map<Integer, Integer> ratings1 = new java.util.HashMap<>();
        ratings1.put(1, 4);
        ratings1.put(2, 5);
        ratings1.put(3, 3);
        
        java.util.Map<Integer, Integer> ratings2 = new java.util.HashMap<>();
        ratings2.put(2, 5);
        ratings2.put(3, 2);
        ratings2.put(4, 4);

        double cosine = engine.calculateCosineSimilarity(ratings1, ratings2);
        Log.d(TAG, "Cosine similarity (user1, user2): " + cosine);
        
        // Test Pearson correlation (Expects Maps)
        double pearson = engine.calculatePearsonCorrelation(ratings1, ratings2);
        Log.d(TAG, "Pearson correlation (user1, user2): " + pearson);
    }
    
    /**
     * Test cold start handling
     */
    private static void testColdStartHandling(CollaborativeFilteringEngine engine) {
        Log.d(TAG, "Testing cold start handling...");
        
        // Test with a user that has no history (should get popular products)
        List<Product> recommendations = engine.getRecommendations(999, 5);
        Log.d(TAG, "Cold start user got " + recommendations.size() + " recommendations (should be popular products)");
    }
    
    /**
     * Test performance caching
     */
    private static void testPerformanceCaching(CollaborativeFilteringEngine engine) {
        Log.d(TAG, "Testing performance caching...");
        
        // First call - should compute and cache
        long startTime = System.currentTimeMillis();
        List<Product> recommendations1 = engine.getRecommendations(1, 5);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        // Second call - should use cache
        List<Product> recommendations2 = engine.getRecommendations(1, 5);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        Log.d(TAG, "First call took " + firstCallTime + "ms, second call took " + secondCallTime + "ms");
        Log.d(TAG, "Cache performance improvement: " + (firstCallTime > secondCallTime ? "YES" : "NO"));
    }
}
