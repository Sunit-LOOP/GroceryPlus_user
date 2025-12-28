package com.sunit.groceryplus.utils;

import com.sunit.groceryplus.models.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class containing advanced search and sorting algorithms for the GroceryPlus app
 */
public class SearchSortAlgorithms {

    /**
     * Performs binary search on a sorted list of products by name
     * Time Complexity: O(log n)
     * 
     * @param sortedProducts Sorted list of products by name
     * @param targetName Name to search for
     * @return Index of the product if found, -1 otherwise
     */
    public static int binarySearchByName(List<Product> sortedProducts, String targetName) {
        int left = 0;
        int right = sortedProducts.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Product midProduct = sortedProducts.get(mid);
            int comparison = midProduct.getProductName().compareToIgnoreCase(targetName);
            
            if (comparison == 0) {
                return mid; // Found the product
            } else if (comparison < 0) {
                left = mid + 1; // Search in the right half
            } else {
                right = mid - 1; // Search in the left half
            }
        }
        
        return -1; // Product not found
    }

    /**
     * Performs linear search with fuzzy matching for partial name matches
     * Time Complexity: O(n)
     * 
     * @param products List of products to search through
     * @param query Search query
     * @return List of products that match the query
     */
    public static List<Product> fuzzySearch(List<Product> products, String query) {
        List<Product> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        if (lowerQuery.isEmpty()) {
            return new ArrayList<>(products);
        }
        
        for (Product product : products) {
            // Check for exact match first
            if (product.getProductName().toLowerCase().contains(lowerQuery)) {
                results.add(product);
                continue;
            }
            
            // Check for word-based matches
            String[] productWords = product.getProductName().toLowerCase().split("\\s+");
            String[] queryWords = lowerQuery.split("\\s+");
            
            boolean match = false;
            for (String queryWord : queryWords) {
                for (String productWord : productWords) {
                    // Partial match with Levenshtein distance for typos
                    if (productWord.startsWith(queryWord) || 
                        levenshteinDistance(productWord, queryWord) <= 2) {
                        match = true;
                        break;
                    }
                }
                if (match) break;
            }
            
            if (match) {
                results.add(product);
            }
        }
        
        return results;
    }

    /**
     * Calculates the Levenshtein distance between two strings
     * Used for fuzzy matching to handle typos
     * Time Complexity: O(m*n) where m and n are lengths of the strings
     * 
     * @param str1 First string
     * @param str2 Second string
     * @return Levenshtein distance
     */
    private static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        // Initialize base cases
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        
        // Fill the DP table
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation needed
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(
                        dp[i - 1][j],    // Deletion
                        dp[i][j - 1]),   // Insertion
                        dp[i - 1][j - 1] // Substitution
                    );
                }
            }
        }
        
        return dp[str1.length()][str2.length()];
    }

    /**
     * Sorts products by name using merge sort algorithm
     * Time Complexity: O(n log n)
     * Stable sort algorithm
     * 
     * @param products List of products to sort
     * @return New sorted list
     */
    public static List<Product> mergeSortByName(List<Product> products) {
        if (products.size() <= 1) {
            return new ArrayList<>(products);
        }
        
        List<Product> sorted = new ArrayList<>(products);
        mergeSort(sorted, 0, sorted.size() - 1, Comparator.comparing(Product::getProductName));
        return sorted;
    }

    /**
     * Recursive merge sort implementation
     */
    private static void mergeSort(List<Product> list, int left, int right, Comparator<Product> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            
            mergeSort(list, left, mid, comparator);
            mergeSort(list, mid + 1, right, comparator);
            
            merge(list, left, mid, right, comparator);
        }
    }

    /**
     * Merge function for merge sort
     */
    private static void merge(List<Product> list, int left, int mid, int right, Comparator<Product> comparator) {
        List<Product> temp = new ArrayList<>();
        int i = left, j = mid + 1;
        
        // Merge the two halves
        while (i <= mid && j <= right) {
            if (comparator.compare(list.get(i), list.get(j)) <= 0) {
                temp.add(list.get(i++));
            } else {
                temp.add(list.get(j++));
            }
        }
        
        // Add remaining elements
        while (i <= mid) temp.add(list.get(i++));
        while (j <= right) temp.add(list.get(j++));
        
        // Copy back to original list
        for (int k = 0; k < temp.size(); k++) {
            list.set(left + k, temp.get(k));
        }
    }

    /**
     * Sorts products by price using quick sort algorithm
     * Time Complexity: Average O(n log n), Worst O(n²)
     * 
     * @param products List of products to sort
     * @return New sorted list
     */
    public static List<Product> quickSortByPrice(List<Product> products) {
        List<Product> sorted = new ArrayList<>(products);
        quickSort(sorted, 0, sorted.size() - 1, Comparator.comparingDouble(Product::getPrice));
        return sorted;
    }

    /**
     * Recursive quick sort implementation
     */
    private static void quickSort(List<Product> list, int low, int high, Comparator<Product> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSort(list, low, pivotIndex - 1, comparator);
            quickSort(list, pivotIndex + 1, high, comparator);
        }
    }

    /**
     * Partition function for quick sort
     */
    private static int partition(List<Product> list, int low, int high, Comparator<Product> comparator) {
        Product pivot = list.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /**
     * Sorts products by multiple criteria using a hybrid approach
     * First by category, then by name within each category
     * 
     * @param products List of products to sort
     * @return New sorted list
     */
    public static List<Product> sortByCategoryAndName(List<Product> products) {
        List<Product> sorted = new ArrayList<>(products);
        Collections.sort(sorted, (p1, p2) -> {
            int categoryComparison = p1.getCategoryName().compareToIgnoreCase(p2.getCategoryName());
            if (categoryComparison != 0) {
                return categoryComparison;
            }
            return p1.getProductName().compareToIgnoreCase(p2.getProductName());
        });
        return sorted;
    }

    /**
     * Sorts products by rating (descending) then by price (ascending)
     * Assumes products have a getRating() method that returns a double
     * 
     * @param products List of products to sort
     * @return New sorted list
     */
    public static List<Product> sortByRatingThenPrice(List<Product> products) {
        List<Product> sorted = new ArrayList<>(products);
        Collections.sort(sorted, (p1, p2) -> {
            // Sort by rating descending
            int ratingComparison = Double.compare(p2.getRating(), p1.getRating());
            if (ratingComparison != 0) {
                return ratingComparison;
            }
            // If ratings are equal, sort by price ascending
            return Double.compare(p1.getPrice(), p2.getPrice());
        });
        return sorted;
    }
}