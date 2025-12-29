package com.sunit.groceryplus;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.ProductAdapter;
import com.sunit.groceryplus.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sunit.groceryplus.utils.SearchSortAlgorithms;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    
    private EditText searchEt;
    private ImageButton sortBtn;
    private RecyclerView searchResultsRv;
    private TextView noResultsTv;
    
    private int userId;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private ProductAdapter productAdapter;
    
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> searchResults = new ArrayList<>();
    
    // History
    private View historyLayout;
    private RecyclerView historyRv;
    private com.sunit.groceryplus.adapters.SearchHistoryAdapter historyAdapter;
    private List<String> recentSearches = new ArrayList<>();
    private com.sunit.groceryplus.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Invalid user session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repositories
        productRepository = new ProductRepository(this);
        cartRepository = new CartRepository(this);
        dbHelper = new com.sunit.groceryplus.DatabaseHelper(this);

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load all products
        loadAllProducts();

        // Setup search listener
        setupSearchListener();
        
        // Setup Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        searchEt = findViewById(R.id.searchEt);
        sortBtn = findViewById(R.id.sortBtn);
        searchResultsRv = findViewById(R.id.searchResultsRv);
        noResultsTv = findViewById(R.id.noResultsTv);
        
        sortBtn.setOnClickListener(v -> showSortDialog());
        
        // Auto-focus search field
        searchEt.requestFocus();

        // History Setup
        historyLayout = findViewById(R.id.historyLayout);
        historyRv = findViewById(R.id.historyRv);
        findViewById(R.id.clearHistoryTv).setOnClickListener(v -> clearSearchHistory());
        setupHistoryRecyclerView();
        loadSearchHistory();
    }

    private void showSortDialog() {
        String[] options = {"Name: A to Z", "Name: Z to A", "Price: Low to High", "Price: High to Low", "Highest Rated"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort Products By");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Name A-Z
                    searchResults = SearchSortAlgorithms.mergeSortByName(searchResults);
                    break;
                case 1: // Name Z-A
                    searchResults = SearchSortAlgorithms.mergeSortByName(searchResults);
                    Collections.reverse(searchResults);
                    break;
                case 2: // Price L-H
                    searchResults = SearchSortAlgorithms.quickSortByPrice(searchResults);
                    break;
                case 3: // Price H-L
                    searchResults = SearchSortAlgorithms.quickSortByPrice(searchResults);
                    Collections.reverse(searchResults);
                    break;
                case 4: // Rating
                    searchResults = SearchSortAlgorithms.sortByRatingThenPrice(searchResults);
                    break;
            }
            productAdapter.updateProducts(searchResults);
        });
        builder.show();
    }

    private void setupRecyclerView() {
        searchResultsRv.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, searchResults, userId);
        searchResultsRv.setAdapter(productAdapter);
    }

    private void loadAllProducts() {
        try {
            allProducts = productRepository.getAllProducts();
            
            if (allProducts != null && !allProducts.isEmpty()) {
                // Initially show all products
                searchResults.clear();
                searchResults.addAll(allProducts);
                productAdapter.updateProducts(searchResults);
                showResults();
                Log.d(TAG, "Loaded " + allProducts.size() + " products");
            } else {
                showNoResults();
                Log.w(TAG, "No products found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading products", e);
            Toast.makeText(this, "Error loading products", Toast.LENGTH_SHORT).show();
            showNoResults();
        }
    }

    private void setupSearchListener() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search as user types
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        // Handle search action on keyboard
        searchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEt.getText().toString().trim();
                if (!query.isEmpty()) {
                    saveSearchQuery(query);
                }
                return true;
            }
            return false;
        });

        // Toggle history visibility
        searchEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && searchEt.getText().toString().isEmpty()) {
                showHistory();
            } else {
                hideHistory();
            }
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Show all products if search is empty
            searchResults.clear();
            searchResults.addAll(allProducts);
            productAdapter.updateProducts(searchResults);
            
            if (searchResults.isEmpty()) {
                showNoResults();
            } else {
                showResults();
            }
            return;
        }

        try {
            // Use advanced fuzzy search algorithm
            List<Product> results = SearchSortAlgorithms.fuzzySearch(allProducts, query.trim());
            
            searchResults.clear();
            if (results != null && !results.isEmpty()) {
                searchResults.addAll(results);
                productAdapter.updateProducts(searchResults);
                showResults();
                Log.d(TAG, "Fuzzy found " + results.size() + " products for query: " + query);
            } else {
                productAdapter.updateProducts(searchResults);
                showNoResults();
                Log.d(TAG, "No products found for query: " + query);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching products", e);
            Toast.makeText(this, "Error searching products", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupHistoryRecyclerView() {
        historyRv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        historyAdapter = new com.sunit.groceryplus.adapters.SearchHistoryAdapter(recentSearches, query -> {
            searchEt.setText(query);
            searchEt.setSelection(query.length());
            performSearch(query);
            hideHistory();
        });
        historyRv.setAdapter(historyAdapter);
    }

    private void loadSearchHistory() {
        Cursor cursor = dbHelper.getRecentSearchQueries(userId, 5);
        recentSearches.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                recentSearches.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        historyAdapter.updateItems(recentSearches);
    }

    private void saveSearchQuery(String query) {
        dbHelper.addSearchQuery(userId, query);
        loadSearchHistory();
    }

    private void clearSearchHistory() {
        dbHelper.clearSearchHistory(userId);
        loadSearchHistory();
        hideHistory();
    }

    private void showHistory() {
        if (!recentSearches.isEmpty()) {
            historyLayout.setVisibility(View.VISIBLE);
            searchResultsRv.setVisibility(View.GONE);
            noResultsTv.setVisibility(View.GONE);
        }
    }

    private void hideHistory() {
        historyLayout.setVisibility(View.GONE);
        if (searchResults.isEmpty()) {
            showNoResults();
        } else {
            showResults();
        }
    }

    private void addToCart(Product product) {
        try {
            boolean success = cartRepository.addToCart(userId, product.getProductId(), 1);
            if (success) {
                Toast.makeText(this, product.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
            Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResults() {
        searchResultsRv.setVisibility(View.VISIBLE);
        noResultsTv.setVisibility(View.GONE);
    }

    private void showNoResults() {
        searchResultsRv.setVisibility(View.GONE);
        noResultsTv.setVisibility(View.VISIBLE);
    }
}