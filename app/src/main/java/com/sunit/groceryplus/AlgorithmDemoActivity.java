package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.utils.SearchSortAlgorithms;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration activity showcasing the search and sorting algorithms
 */
public class AlgorithmDemoActivity extends AppCompatActivity {
    private static final String TAG = "AlgorithmDemo";
    
    private TextView resultsTv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm_demo);
        
        resultsTv = findViewById(R.id.resultsTv);
        
        // Run demonstrations
        demonstrateSearchAlgorithms();
        demonstrateSortAlgorithms();
    }
    
    /**
     * Demonstrates the search algorithms
     */
    private void demonstrateSearchAlgorithms() {
        // Create sample products
        List<Product> products = createSampleProducts();
        
        StringBuilder results = new StringBuilder();
        results.append("=== SEARCH ALGORITHM DEMONSTRATIONS ===\n\n");
        
        // Demonstrate fuzzy search
        results.append("1. Fuzzy Search Results for 'Apple':\n");
        List<Product> fuzzyResults = SearchSortAlgorithms.fuzzySearch(products, "Apple");
        for (Product product : fuzzyResults) {
            results.append("   - ").append(product.getProductName()).append("\n");
        }
        
        results.append("\n2. Fuzzy Search Results for 'Juice':\n");
        fuzzyResults = SearchSortAlgorithms.fuzzySearch(products, "Juice");
        for (Product product : fuzzyResults) {
            results.append("   - ").append(product.getProductName()).append("\n");
        }
        
        // Demonstrate binary search (requires sorted list)
        results.append("\n3. Binary Search (requires sorted list):\n");
        List<Product> sortedByName = SearchSortAlgorithms.mergeSortByName(products);
        int index = SearchSortAlgorithms.binarySearchByName(sortedByName, "Organic Apple");
        if (index != -1) {
            results.append("   Found 'Organic Apple' at index: ").append(index).append("\n");
        } else {
            results.append("   'Organic Apple' not found\n");
        }
        
        resultsTv.setText(results.toString());
        Log.d(TAG, results.toString());
    }
    
    /**
     * Demonstrates the sorting algorithms
     */
    private void demonstrateSortAlgorithms() {
        // Create sample products
        List<Product> products = createSampleProducts();
        
        StringBuilder results = new StringBuilder();
        results.append("\n\n=== SORTING ALGORITHM DEMONSTRATIONS ===\n\n");
        
        // Demonstrate merge sort by name
        results.append("1. Merge Sort by Name:\n");
        List<Product> sortedByName = SearchSortAlgorithms.mergeSortByName(new ArrayList<>(products));
        for (Product product : sortedByName) {
            results.append("   - ").append(product.getProductName()).append("\n");
        }
        
        // Demonstrate quick sort by price
        results.append("\n2. Quick Sort by Price:\n");
        List<Product> sortedByPrice = SearchSortAlgorithms.quickSortByPrice(new ArrayList<>(products));
        for (Product product : sortedByPrice) {
            results.append("   - ").append(product.getProductName())
                  .append(" (Rs. ").append(product.getPrice()).append(")\n");
        }
        
        // Demonstrate sort by rating then price
        results.append("\n3. Sort by Rating then Price:\n");
        List<Product> sortedByRating = SearchSortAlgorithms.sortByRatingThenPrice(new ArrayList<>(products));
        for (Product product : sortedByRating) {
            results.append("   - ").append(product.getProductName())
                  .append(" (Rating: ").append(product.getRating())
                  .append(", Rs. ").append(product.getPrice()).append(")\n");
        }
        
        // Demonstrate sort by category and name
        results.append("\n4. Sort by Category and Name:\n");
        List<Product> sortedByCategory = SearchSortAlgorithms.sortByCategoryAndName(new ArrayList<>(products));
        String currentCategory = "";
        for (Product product : sortedByCategory) {
            if (!product.getCategoryName().equals(currentCategory)) {
                currentCategory = product.getCategoryName();
                results.append("\n   Category: ").append(currentCategory).append("\n");
            }
            results.append("     - ").append(product.getProductName()).append("\n");
        }
        
        // Append to existing text
        resultsTv.append(results.toString());
        Log.d(TAG, results.toString());
    }
    
    /**
     * Creates sample products for demonstration
     */
    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        // Add sample products
products.add(new Product(1, "Organic Apple", 1, "Fruits", 120.0, "Fresh organic apples", "apple.jpg", 4.8, 50, 1, "General Store"));
        products.add(new Product(2, "Banana", 1, "Fruits", 60.0, "Ripe bananas", "banana.jpg", 4.5, 100, 1, "General Store"));
        products.add(new Product(3, "Orange Juice", 2, "Beverages", 80.0, "Fresh orange juice", "juice_bottle.jpg", 4.7, 30, 1, "General Store"));
        products.add(new Product(4, "Whole Wheat Bread", 3, "Bakery", 90.0, "Healthy whole wheat bread", "bread.jpg", 4.6, 25, 1, "General Store"));
        products.add(new Product(5, "Milk", 4, "Dairy", 70.0, "Fresh pasteurized milk", "bottle_milk.png", 4.4, 40, 1, "General Store"));
        products.add(new Product(6, "Greek Yogurt", 4, "Dairy", 110.0, "Creamy Greek yogurt", "curd.png", 4.9, 35, 1, "General Store"));
products.add(new Product(7, "Basmati Rice", 5, "Staples", 200.0, "Premium basmati rice", "rice_sack.jpg", 4.3, 20, 1, "General Store"));
        products.add(new Product(8, "Olive Oil", 6, "Cooking Essentials", 350.0, "Extra virgin olive oil", "oil_bottle.jpg", 4.8, 15, 1, "General Store"));
        
        return products;
    }
}