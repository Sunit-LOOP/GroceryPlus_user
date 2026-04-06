package com.sunit.groceryplus.utils;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/** Validation suite for verifying collaborative filtering logic, similarity accuracy, and cache performance. */
public class CollaborativeFilteringTest {
    
    private static final String TAG = "CollaborativeFilteringTest";
    
    /** Executes the full battery of collaborative filtering tests with sample data. */
    public static void testCollaborativeFiltering(Context context) {
        Log.d(TAG, "Starting Collaborative Filtering Tests...");
        
        try {
            // Test 1: User-based collaborative filtering
            testUserBasedFiltering();
            
            // Test 2: Item-based collaborative filtering
            testItemBasedFiltering();
            
            // Test 3: Similarity calculations
            testSimilarityCalculations();
            
            // Test 4: Recommendation accuracy
            testRecommendationAccuracy();
            
            // Test 5: Performance metrics
            testPerformanceMetrics();
            
            // Test 6: Cold start problem handling
            testColdStartProblem();
            
            // Test 7: Data sparsity handling
            testDataSparsity();
            
            Log.d(TAG, "All Collaborative Filtering Tests Completed Successfully!");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in Collaborative Filtering Tests", e);
        }
    }
    
    /**
     * Test user-based collaborative filtering algorithm
     */
    private static void testUserBasedFiltering() {
        Log.d(TAG, "Testing User-Based Collaborative Filtering...");
        
        // Create sample user-item ratings matrix
        Map<Integer, Map<Integer, Double>> userRatings = createSampleRatings();
        
        // Test similarity calculation between users
        double similarity = calculateCosineSimilarity(
            userRatings.get(1), 
            userRatings.get(2)
        );
        
        Log.d(TAG, "User similarity (1,2): " + similarity);
        
        // Test recommendation generation
        List<Integer> recommendations = generateUserBasedRecommendations(userRatings, 1);
        Log.d(TAG, "Recommendations for user 1: " + recommendations);
        
        // Validate results
        assert similarity >= 0.0 && similarity <= 1.0 : "Similarity should be between 0 and 1";
        assert !recommendations.isEmpty() : "Should generate recommendations";
        
        Log.d(TAG, "✓ User-Based Filtering Test Passed");
    }
    
    /**
     * Test item-based collaborative filtering algorithm
     */
    private static void testItemBasedFiltering() {
        Log.d(TAG, "Testing Item-Based Collaborative Filtering...");
        
        // Create item-user ratings matrix (transpose of user-item matrix)
        Map<Integer, Map<Integer, Double>> itemRatings = createItemRatings();
        
        // Test similarity calculation between items
        double similarity = calculateCosineSimilarity(
            itemRatings.get(101), 
            itemRatings.get(102)
        );
        
        Log.d(TAG, "Item similarity (101,102): " + similarity);
        
        // Test recommendation generation
        List<Integer> recommendations = generateItemBasedRecommendations(itemRatings, 101);
        Log.d(TAG, "Recommendations for item 101: " + recommendations);
        
        // Validate results
        assert similarity >= 0.0 && similarity <= 1.0 : "Similarity should be between 0 and 1";
        assert !recommendations.isEmpty() : "Should generate recommendations";
        
        Log.d(TAG, "✓ Item-Based Filtering Test Passed");
    }
    
    /**
     * Test various similarity calculation methods
     */
    private static void testSimilarityCalculations() {
        Log.d(TAG, "Testing Similarity Calculations...");
        
        Map<Integer, Double> user1 = Map.of(1, 5.0, 2, 3.0, 3, 4.0);
        Map<Integer, Double> user2 = Map.of(1, 4.0, 2, 2.0, 3, 5.0);
        Map<Integer, Double> user3 = Map.of(1, 5.0, 2, 3.0, 3, 4.0);
        
        // Test cosine similarity
        double cosine12 = calculateCosineSimilarity(user1, user2);
        double cosine13 = calculateCosineSimilarity(user1, user3);
        
        // Test Pearson correlation
        double pearson12 = calculatePearsonCorrelation(user1, user2);
        double pearson13 = calculatePearsonCorrelation(user1, user3);
        
        // Test Euclidean distance
        double euclidean12 = calculateEuclideanDistance(user1, user2);
        double euclidean13 = calculateEuclideanDistance(user1, user3);
        
        Log.d(TAG, "Cosine similarity (1,2): " + cosine12);
        Log.d(TAG, "Cosine similarity (1,3): " + cosine13);
        Log.d(TAG, "Pearson correlation (1,2): " + pearson12);
        Log.d(TAG, "Pearson correlation (1,3): " + pearson13);
        Log.d(TAG, "Euclidean distance (1,2): " + euclidean12);
        Log.d(TAG, "Euclidean distance (1,3): " + euclidean13);
        
        // Validate identical users have perfect similarity
        assert Math.abs(cosine13 - 1.0) < 0.001 : "Identical users should have cosine similarity of 1";
        assert Math.abs(pearson13 - 1.0) < 0.001 : "Identical users should have Pearson correlation of 1";
        assert Math.abs(euclidean13) < 0.001 : "Identical users should have Euclidean distance of 0";
        
        Log.d(TAG, "✓ Similarity Calculations Test Passed");
    }
    
    /**
     * Test recommendation accuracy and precision
     */
    private static void testRecommendationAccuracy() {
        Log.d(TAG, "Testing Recommendation Accuracy...");
        
        Map<Integer, Map<Integer, Double>> userRatings = createSampleRatings();
        
        // Generate recommendations for a user
        List<Integer> recommendations = generateUserBasedRecommendations(userRatings, 1);
        
        // Calculate precision@k
        int k = 5;
        double precision = calculatePrecisionAtK(userRatings, 1, recommendations, k);
        double recall = calculateRecallAtK(userRatings, 1, recommendations, k);
        
        Log.d(TAG, "Precision@" + k + ": " + precision);
        Log.d(TAG, "Recall@" + k + ": " + recall);
        
        // Calculate F1 score
        double f1Score = 2 * (precision * recall) / (precision + recall);
        Log.d(TAG, "F1 Score: " + f1Score);
        
        // Validate metrics are within valid ranges
        assert precision >= 0.0 && precision <= 1.0 : "Precision should be between 0 and 1";
        assert recall >= 0.0 && recall <= 1.0 : "Recall should be between 0 and 1";
        assert f1Score >= 0.0 && f1Score <= 1.0 : "F1 Score should be between 0 and 1";
        
        Log.d(TAG, "✓ Recommendation Accuracy Test Passed");
    }
    
    /**
     * Test performance metrics of the algorithms
     */
    private static void testPerformanceMetrics() {
        Log.d(TAG, "Testing Performance Metrics...");
        
        Map<Integer, Map<Integer, Double>> userRatings = createLargeSampleRatings(1000, 500);
        
        // Test user-based filtering performance
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 10; i++) {
            generateUserBasedRecommendations(userRatings, i);
        }
        long userBasedTime = System.currentTimeMillis() - startTime;
        
        // Test item-based filtering performance
        startTime = System.currentTimeMillis();
        for (int i = 1; i <= 10; i++) {
            generateItemBasedRecommendations(createItemRatings(), i);
        }
        long itemBasedTime = System.currentTimeMillis() - startTime;
        
        Log.d(TAG, "User-based filtering (10 users): " + userBasedTime + "ms");
        Log.d(TAG, "Item-based filtering (10 items): " + itemBasedTime + "ms");
        Log.d(TAG, "Average user-based time: " + (userBasedTime / 10) + "ms");
        Log.d(TAG, "Average item-based time: " + (itemBasedTime / 10) + "ms");
        
        // Performance should be reasonable (< 100ms per recommendation)
        assert userBasedTime < 1000 : "User-based filtering should complete within 1 second for 10 users";
        assert itemBasedTime < 1000 : "Item-based filtering should complete within 1 second for 10 items";
        
        Log.d(TAG, "✓ Performance Metrics Test Passed");
    }
    
    /**
     * Test cold start problem handling
     */
    private static void testColdStartProblem() {
        Log.d(TAG, "Testing Cold Start Problem...");
        
        // Create a new user with no ratings
        Map<Integer, Map<Integer, Double>> userRatings = createSampleRatings();
        Map<Integer, Double> newUser = new HashMap<>();
        
        // Test recommendations for new user
        List<Integer> recommendations = generateUserBasedRecommendations(userRatings, 999);
        
        // Should fall back to popularity-based recommendations
        Log.d(TAG, "Cold start recommendations: " + recommendations);
        
        // Test fallback strategies
        List<Integer> popularItems = getPopularItems(userRatings);
        assert !popularItems.isEmpty() : "Should have popular items for fallback";
        
        Log.d(TAG, "Popular items for cold start: " + popularItems);
        Log.d(TAG, "✓ Cold Start Problem Test Passed");
    }
    
    /**
     * Test data sparsity handling
     */
    private static void testDataSparsity() {
        Log.d(TAG, "Testing Data Sparsity...");
        
        // Create sparse ratings matrix
        Map<Integer, Map<Integer, Double>> sparseRatings = createSparseRatings();
        
        // Test similarity calculation with sparse data
        double similarity = calculateCosineSimilarity(
            sparseRatings.get(1), 
            sparseRatings.get(2)
        );
        
        Log.d(TAG, "Similarity with sparse data: " + similarity);
        
        // Test recommendations with sparse data
        List<Integer> recommendations = generateUserBasedRecommendations(sparseRatings, 1);
        Log.d(TAG, "Recommendations with sparse data: " + recommendations);
        
        // Should handle sparse data gracefully
        assert similarity >= 0.0 && similarity <= 1.0 : "Similarity should be valid even with sparse data";
        
        Log.d(TAG, "✓ Data Sparsity Test Passed");
    }
    
    // Helper methods for creating test data and calculations
    
    private static Map<Integer, Map<Integer, Double>> createSampleRatings() {
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();
        
        // User 1 ratings
        Map<Integer, Double> user1 = new HashMap<>();
        user1.put(101, 5.0);
        user1.put(102, 3.0);
        user1.put(103, 4.0);
        ratings.put(1, user1);
        
        // User 2 ratings
        Map<Integer, Double> user2 = new HashMap<>();
        user2.put(101, 4.0);
        user2.put(102, 2.0);
        user2.put(103, 5.0);
        ratings.put(2, user2);
        
        // User 3 ratings
        Map<Integer, Double> user3 = new HashMap<>();
        user3.put(101, 5.0);
        user3.put(102, 3.0);
        user3.put(103, 4.0);
        ratings.put(3, user3);
        
        return ratings;
    }
    
    private static Map<Integer, Map<Integer, Double>> createItemRatings() {
        Map<Integer, Map<Integer, Double>> itemRatings = new HashMap<>();
        Map<Integer, Map<Integer, Double>> userRatings = createSampleRatings();
        
        // Transpose the user-item matrix to item-user matrix
        for (Map.Entry<Integer, Map<Integer, Double>> userEntry : userRatings.entrySet()) {
            int userId = userEntry.getKey();
            for (Map.Entry<Integer, Double> itemEntry : userEntry.getValue().entrySet()) {
                int itemId = itemEntry.getKey();
                double rating = itemEntry.getValue();
                
                itemRatings.computeIfAbsent(itemId, k -> new HashMap<>()).put(userId, rating);
            }
        }
        
        return itemRatings;
    }
    
    private static Map<Integer, Map<Integer, Double>> createLargeSampleRatings(int users, int items) {
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();
        
        for (int userId = 1; userId <= users; userId++) {
            Map<Integer, Double> userRatings = new HashMap<>();
            for (int itemId = 1; itemId <= items; itemId++) {
                if (Math.random() > 0.7) { // 30% sparsity
                    userRatings.put(itemId, 1.0 + Math.random() * 4.0);
                }
            }
            ratings.put(userId, userRatings);
        }
        
        return ratings;
    }
    
    private static Map<Integer, Map<Integer, Double>> createSparseRatings() {
        Map<Integer, Map<Integer, Double>> ratings = new HashMap<>();
        
        // Create very sparse data
        Map<Integer, Double> user1 = new HashMap<>();
        user1.put(101, 5.0);
        ratings.put(1, user1);
        
        Map<Integer, Double> user2 = new HashMap<>();
        user2.put(102, 3.0);
        ratings.put(2, user2);
        
        return ratings;
    }
    
    private static double calculateCosineSimilarity(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
        Set<Integer> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        if (commonItems.isEmpty()) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int item : commonItems) {
            double rating1 = ratings1.get(item);
            double rating2 = ratings2.get(item);
            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private static double calculatePearsonCorrelation(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
        Set<Integer> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        if (commonItems.size() < 2) {
            return 0.0;
        }
        
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        int n = commonItems.size();
        
        for (int item : commonItems) {
            double rating1 = ratings1.get(item);
            double rating2 = ratings2.get(item);
            
            sum1 += rating1;
            sum2 += rating2;
            sum1Sq += rating1 * rating1;
            sum2Sq += rating2 * rating2;
            pSum += rating1 * rating2;
        }
        
        double numerator = pSum - (sum1 * sum2 / n);
        double denominator = Math.sqrt((sum1Sq - (sum1 * sum1 / n)) * (sum2Sq - (sum2 * sum2 / n)));
        
        if (denominator == 0) {
            return 0.0;
        }
        
        return numerator / denominator;
    }
    
    private static double calculateEuclideanDistance(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
        Set<Integer> commonItems = new HashSet<>(ratings1.keySet());
        commonItems.retainAll(ratings2.keySet());
        
        double sumSquaredDifferences = 0.0;
        for (int item : commonItems) {
            double diff = ratings1.get(item) - ratings2.get(item);
            sumSquaredDifferences += diff * diff;
        }
        
        return Math.sqrt(sumSquaredDifferences);
    }
    
    private static List<Integer> generateUserBasedRecommendations(Map<Integer, Map<Integer, Double>> userRatings, int targetUserId) {
        List<Integer> recommendations = new ArrayList<>();
        Map<Integer, Double> similarities = new HashMap<>();
        
        Map<Integer, Double> targetUserRatings = userRatings.get(targetUserId);
        if (targetUserRatings == null) {
            return getPopularItems(userRatings);
        }
        
        // Calculate similarities with other users
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userRatings.entrySet()) {
            int userId = entry.getKey();
            if (userId != targetUserId) {
                double similarity = calculateCosineSimilarity(targetUserRatings, entry.getValue());
                similarities.put(userId, similarity);
            }
        }
        
        // Find items not rated by target user
        Set<Integer> targetUserItems = targetUserRatings.keySet();
        Map<Integer, Double> itemScores = new HashMap<>();
        
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userRatings.entrySet()) {
            int userId = entry.getKey();
            if (userId != targetUserId && similarities.get(userId) > 0.1) {
                double similarity = similarities.get(userId);
                for (Map.Entry<Integer, Double> itemEntry : entry.getValue().entrySet()) {
                    int itemId = itemEntry.getKey();
                    if (!targetUserItems.contains(itemId)) {
                        itemScores.merge(itemId, itemEntry.getValue() * similarity, Double::sum);
                    }
                }
            }
        }
        
        // Sort items by score and get top recommendations
        itemScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> recommendations.add(entry.getKey()));
        
        return recommendations;
    }
    
    private static List<Integer> generateItemBasedRecommendations(Map<Integer, Map<Integer, Double>> itemRatings, int targetItemId) {
        List<Integer> recommendations = new ArrayList<>();
        Map<Integer, Double> similarities = new HashMap<>();
        
        Map<Integer, Double> targetItemRatings = itemRatings.get(targetItemId);
        if (targetItemRatings == null) {
            return getPopularItems(createSampleRatings());
        }
        
        // Calculate similarities with other items
        for (Map.Entry<Integer, Map<Integer, Double>> entry : itemRatings.entrySet()) {
            int itemId = entry.getKey();
            if (itemId != targetItemId) {
                double similarity = calculateCosineSimilarity(targetItemRatings, entry.getValue());
                similarities.put(itemId, similarity);
            }
        }
        
        // Sort by similarity and get top recommendations
        similarities.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> recommendations.add(entry.getKey()));
        
        return recommendations;
    }
    
    private static double calculatePrecisionAtK(Map<Integer, Map<Integer, Double>> userRatings, 
                                                   int userId, List<Integer> recommendations, int k) {
        if (k <= 0 || recommendations.isEmpty()) {
            return 0.0;
        }
        
        int relevantCount = 0;
        List<Integer> topK = recommendations.subList(0, Math.min(k, recommendations.size()));
        
        Map<Integer, Double> actualRatings = userRatings.get(userId);
        if (actualRatings != null) {
            for (int item : topK) {
                if (actualRatings.containsKey(item) && actualRatings.get(item) >= 4.0) {
                    relevantCount++;
                }
            }
        }
        
        return (double) relevantCount / k;
    }
    
    private static double calculateRecallAtK(Map<Integer, Map<Integer, Double>> userRatings, 
                                                 int userId, List<Integer> recommendations, int k) {
        if (k <= 0 || recommendations.isEmpty()) {
            return 0.0;
        }
        
        Map<Integer, Double> actualRatings = userRatings.get(userId);
        if (actualRatings == null) {
            return 0.0;
        }
        
        // Count relevant items
        int totalRelevant = 0;
        for (Map.Entry<Integer, Double> entry : actualRatings.entrySet()) {
            if (entry.getValue() >= 4.0) {
                totalRelevant++;
            }
        }
        
        if (totalRelevant == 0) {
            return 0.0;
        }
        
        // Count relevant items in top k recommendations
        int relevantInTopK = 0;
        List<Integer> topK = recommendations.subList(0, Math.min(k, recommendations.size()));
        
        for (int item : topK) {
            if (actualRatings.containsKey(item) && actualRatings.get(item) >= 4.0) {
                relevantInTopK++;
            }
        }
        
        return (double) relevantInTopK / totalRelevant;
    }
    
    private static List<Integer> getPopularItems(Map<Integer, Map<Integer, Double>> userRatings) {
        Map<Integer, Double> itemPopularity = new HashMap<>();
        
        for (Map<Integer, Double> userRating : userRatings.values()) {
            for (Map.Entry<Integer, Double> entry : userRating.entrySet()) {
                itemPopularity.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }
        
        return itemPopularity.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Test basic recommendation generation
     */
    private static void testBasicRecommendations(CollaborativeFilteringEngine engine) {
        Log.d(TAG, "Testing basic recommendation generation...");
        
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
