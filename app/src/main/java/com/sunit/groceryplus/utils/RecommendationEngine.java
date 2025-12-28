package com.sunit.groceryplus.utils;

import android.content.Context;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.models.Product;
import java.util.*;

/**
 * Advanced Recommendation Engine for GroceryPlus
 * implements Hybrid Filtering (Content-Based + Collaborative)
 */
public class RecommendationEngine {
    private DatabaseHelper dbHelper;
    private Context context;

    public RecommendationEngine(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Gets personalized recommendations for a user
     * @param userId The ID of the user
     * @param limit Maximum number of recommendations to return
     * @return List of recommended products
     */
    public List<Product> getRecommendations(int userId, int limit) {
        List<Product> recommendations = new ArrayList<>();
        Set<Integer> recommendedProductIds = new HashSet<>();

        // 1. Content-Based: Recommendations based on user's own history
        List<Product> contentBased = getContentBasedRecommendations(userId);
        for (Product p : contentBased) {
            if (recommendedProductIds.add(p.getProductId())) {
                recommendations.add(p);
            }
            if (recommendations.size() >= limit / 2) break;
        }

        // 2. Collaborative Filtering: Recommendations based on similar users
        List<Product> collaborative = getCollaborativeRecommendations(userId);
        for (Product p : collaborative) {
            if (recommendedProductIds.add(p.getProductId())) {
                recommendations.add(p);
            }
            if (recommendations.size() >= limit) break;
        }

        // 3. Fallback: Popular products if not enough personalized results
        if (recommendations.size() < limit) {
            List<Product> allProducts = dbHelper.getAllProducts();
            Collections.shuffle(allProducts); // Randomize for variety
            for (Product p : allProducts) {
                if (recommendedProductIds.add(p.getProductId())) {
                    recommendations.add(p);
                }
                if (recommendations.size() >= limit) break;
            }
        }

        return recommendations;
    }

    /**
     * Content-Based Filtering: Find products in categories the user frequently buys from
     */
    private List<Product> getContentBasedRecommendations(int userId) {
        List<Product> results = new ArrayList<>();
        Map<Integer, Integer> categoryWeights = new HashMap<>();

        // Analyze user's previous orders
        List<com.sunit.groceryplus.models.Order> orders = dbHelper.getOrdersByUser(userId);
        for (com.sunit.groceryplus.models.Order order : orders) {
            List<OrderItem> items = dbHelper.getOrderItems(order.getOrderId());
            for (OrderItem item : items) {
                Product p = dbHelper.getProductById(item.getProductId());
                if (p != null) {
                    int catId = p.getCategoryId();
                    categoryWeights.put(catId, categoryWeights.getOrDefault(catId, 0) + item.getQuantity());
                }
            }
        }

        // Get top categories
        List<Map.Entry<Integer, Integer>> sortedCategories = new ArrayList<>(categoryWeights.entrySet());
        Collections.sort(sortedCategories, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Fetch products from favorite categories
        for (int i = 0; i < Math.min(2, sortedCategories.size()); i++) {
            results.addAll(dbHelper.getProductsByCategory(sortedCategories.get(i).getKey()));
        }

        return results;
    }

    /**
     * Simple Collaborative Filtering: "Users who bought this also bought..."
     */
    private List<Product> getCollaborativeRecommendations(int userId) {
        List<Product> results = new ArrayList<>();
        Set<Integer> userBoughtIds = new HashSet<>();
        
        // Get products current user bought
        List<com.sunit.groceryplus.models.Order> userOrders = dbHelper.getOrdersByUser(userId);
        for (com.sunit.groceryplus.models.Order order : userOrders) {
            for (OrderItem item : dbHelper.getOrderItems(order.getOrderId())) {
                userBoughtIds.add(item.getProductId());
            }
        }

        // Find other users who bought at least one of these products
        Map<Integer, Integer> otherUserScores = new HashMap<>();
        List<com.sunit.groceryplus.models.User> allUsers = dbHelper.getAllUsers();
        
        for (com.sunit.groceryplus.models.User otherUser : allUsers) {
            if (otherUser.getUserId() == userId) continue;
            
            int overlap = 0;
            List<com.sunit.groceryplus.models.Order> otherOrders = dbHelper.getOrdersByUser(otherUser.getUserId());
            for (com.sunit.groceryplus.models.Order order : otherOrders) {
                for (OrderItem item : dbHelper.getOrderItems(order.getOrderId())) {
                    if (userBoughtIds.contains(item.getProductId())) {
                        overlap++;
                    }
                }
            }
            if (overlap > 0) {
                otherUserScores.put(otherUser.getUserId(), overlap);
            }
        }

        // Get top similar user
        Integer topSimilarUser = null;
        int maxOverlap = -1;
        for (Map.Entry<Integer, Integer> entry : otherUserScores.entrySet()) {
            if (entry.getValue() > maxOverlap) {
                maxOverlap = entry.getValue();
                topSimilarUser = entry.getKey();
            }
        }

        // Recommend products from most similar user that current user hasn't bought
        if (topSimilarUser != null) {
            List<com.sunit.groceryplus.models.Order> similarOrders = dbHelper.getOrdersByUser(topSimilarUser);
            for (com.sunit.groceryplus.models.Order order : similarOrders) {
                for (OrderItem item : dbHelper.getOrderItems(order.getOrderId())) {
                    if (!userBoughtIds.contains(item.getProductId())) {
                        Product p = dbHelper.getProductById(item.getProductId());
                        if (p != null) results.add(p);
                    }
                }
            }
        }

        return results;
    }
}
