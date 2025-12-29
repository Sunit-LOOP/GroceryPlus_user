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
    private static final int K_NEIGHBORS = 5;

    public RecommendationEngine(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Gets personalized recommendations using Collaborative Filtering
     */
    public List<Product> getRecommendations(int userId, int limit) {
        List<Product> recommendations = new ArrayList<>();
        Set<Integer> recommendedProductIds = new HashSet<>();

        // 1. Get Purchase History and Ratings for all users
        Map<Integer, Map<Integer, Integer>> purchaseHistory = dbHelper.getAllUserPurchaseHistory();
        List<com.sunit.groceryplus.models.Review> allReviews = dbHelper.getAllReviewsForRecommendations();
        
        // Transform reviews into User-Item rating map
        Map<Integer, Map<Integer, Integer>> ratingMatrix = new HashMap<>();
        for (com.sunit.groceryplus.models.Review review : allReviews) {
            if (!ratingMatrix.containsKey(review.getUserId())) {
                ratingMatrix.put(review.getUserId(), new HashMap<>());
            }
            ratingMatrix.get(review.getUserId()).put(review.getProductId(), review.getRating());
        }

        // 2. Compute Similarities
        Map<Integer, Double> similarities = computeUserSimilarities(userId, purchaseHistory, ratingMatrix);

        // 3. Get recommendations from top K neighbors
        if (!similarities.isEmpty()) {
            List<Map.Entry<Integer, Double>> sortedSimilarities = new ArrayList<>(similarities.entrySet());
            Collections.sort(sortedSimilarities, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            Set<Integer> userBoughtIds = purchaseHistory.getOrDefault(userId, new HashMap<>()).keySet();
            Map<Integer, Double> productScores = new HashMap<>();

            int neighborsCount = Math.min(K_NEIGHBORS, sortedSimilarities.size());
            for (int i = 0; i < neighborsCount; i++) {
                int neighborId = sortedSimilarities.get(i).getKey();
                double similarity = sortedSimilarities.get(i).getValue();

                Map<Integer, Integer> neighborPurchases = purchaseHistory.getOrDefault(neighborId, new HashMap<>());
                for (int productId : neighborPurchases.keySet()) {
                    if (!userBoughtIds.contains(productId)) {
                        productScores.put(productId, productScores.getOrDefault(productId, 0.0) + similarity);
                    }
                }
                
                // Also consider neighbor ratings
                Map<Integer, Integer> neighborRatings = ratingMatrix.getOrDefault(neighborId, new HashMap<>());
                for (Map.Entry<Integer, Integer> entry : neighborRatings.entrySet()) {
                    int productId = entry.getKey();
                    int rating = entry.getValue();
                    if (!userBoughtIds.contains(productId)) {
                        // Weighted score: similarity * (rating / 5.0)
                        double score = similarity * (rating / 5.0);
                        productScores.put(productId, productScores.getOrDefault(productId, 0.0) + score);
                    }
                }
            }

            // Sort products by score
            List<Map.Entry<Integer, Double>> sortedProducts = new ArrayList<>(productScores.entrySet());
            Collections.sort(sortedProducts, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            for (Map.Entry<Integer, Double> entry : sortedProducts) {
                Product p = dbHelper.getProductById(entry.getKey());
                if (p != null) {
                    recommendations.add(p);
                    recommendedProductIds.add(p.getProductId());
                }
                if (recommendations.size() >= limit) break;
            }
        }

        // 4. Fallback: Popular products if not enough personalized results (Cold Start)
        if (recommendations.size() < limit) {
            List<Product> allProducts = dbHelper.getAllProducts();
            // In a real app, we would sort by actual popularity (sales count)
            Collections.shuffle(allProducts); 
            for (Product p : allProducts) {
                if (recommendedProductIds.add(p.getProductId())) {
                    recommendations.add(p);
                }
                if (recommendations.size() >= limit) break;
            }
        }

        return recommendations;
    }

    private Map<Integer, Double> computeUserSimilarities(int targetUserId, 
                                                       Map<Integer, Map<Integer, Integer>> purchaseHistory,
                                                       Map<Integer, Map<Integer, Integer>> ratingMatrix) {
        Map<Integer, Double> similarities = new HashMap<>();
        Map<Integer, Integer> targetPurchases = purchaseHistory.getOrDefault(targetUserId, new HashMap<>());
        Map<Integer, Integer> targetRatings = ratingMatrix.getOrDefault(targetUserId, new HashMap<>());

        Set<Integer> allUsers = new HashSet<>(purchaseHistory.keySet());
        allUsers.addAll(ratingMatrix.keySet());

        for (int otherUserId : allUsers) {
            if (otherUserId == targetUserId) continue;

            double jaccard = calculateJaccard(targetPurchases.keySet(), purchaseHistory.getOrDefault(otherUserId, new HashMap<>()).keySet());
            double cosine = calculateCosine(targetRatings, ratingMatrix.getOrDefault(otherUserId, new HashMap<>()));

            // Weighted combination (40% purchase overlap, 60% rating similarity)
            double combinedSimilarity = (0.4 * jaccard) + (0.6 * cosine);
            
            if (combinedSimilarity > 0) {
                similarities.put(otherUserId, combinedSimilarity);
            }
        }
        return similarities;
    }

    private double calculateJaccard(Set<Integer> set1, Set<Integer> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0;
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private double calculateCosine(Map<Integer, Integer> ratings1, Map<Integer, Integer> ratings2) {
        if (ratings1.isEmpty() || ratings2.isEmpty()) return 0;
        
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        Set<Integer> allProducts = new HashSet<>(ratings1.keySet());
        allProducts.addAll(ratings2.keySet());

        for (int productId : allProducts) {
            int r1 = ratings1.getOrDefault(productId, 0);
            int r2 = ratings2.getOrDefault(productId, 0);
            dotProduct += r1 * r2;
            norm1 += r1 * r1;
            norm2 += r2 * r2;
        }

        if (norm1 == 0 || norm2 == 0) return 0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
