package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.ProductAdapter;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "FavoritesActivity";
    
    private RecyclerView favoritesRv;
    private TextView emptyFavoritesTv;
    
    private int userId;
    private FavoriteRepository favoriteRepository;
    private CartRepository cartRepository;
    private ProductAdapter productAdapter;
    private ApiService apiService;

    private List<Product> favoriteProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Get user ID from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Invalid user session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize repositories
        favoriteRepository = new FavoriteRepository(this);
        cartRepository = new CartRepository(this);
        apiService = new ApiService(this);

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load favorites
        loadFavorites();
        
        // Setup Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorites");
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

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when returning to this activity
        loadFavorites();
    }

    private void initViews() {
        favoritesRv = findViewById(R.id.favoritesRv);
        emptyFavoritesTv = findViewById(R.id.emptyFavoritesTv);
    }

    private void setupRecyclerView() {
        favoritesRv.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, favoriteProducts, userId);
        favoritesRv.setAdapter(productAdapter);
    }

    private void loadFavorites() {
        // For now, use database implementation
        // API integration can be added later when API structure is confirmed
        try {
            favoriteProducts.clear();
            List<Product> products = favoriteRepository.getFavoriteProducts(userId);

            if (products != null && !products.isEmpty()) {
                favoriteProducts.addAll(products);
                productAdapter.updateProducts(favoriteProducts);
                showFavorites();
            } else {
                showEmptyFavorites();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading favorites", e);
            Toast.makeText(this, "Error loading favorites", Toast.LENGTH_SHORT).show();
            showEmptyFavorites();
        }
    }

    private void showFavorites() {
        favoritesRv.setVisibility(View.VISIBLE);
        emptyFavoritesTv.setVisibility(View.GONE);
    }

    private void showEmptyFavorites() {
        favoritesRv.setVisibility(View.GONE);
        emptyFavoritesTv.setVisibility(View.VISIBLE);
    }
}
