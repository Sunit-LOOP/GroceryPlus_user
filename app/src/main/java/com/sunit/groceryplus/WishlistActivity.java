package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.ProductAdapter;
import com.sunit.groceryplus.models.Product;

import java.util.List;


/**
 * WishlistActivity - Manages the user's wishlist items.
 * 
 * This activity displays a collection of products that the user has saved for later purchase.
 * It allows users to view their wishlist in a grid layout and access product details.
 * It handles loading data from the WishlistRepository and displaying appropriate empty states.
 * 
 * Key Features:
 * - View wishlist products
 * - Grid layout display
 * - Empty wishlist handling
 * - Navigation to product details
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class WishlistActivity extends AppCompatActivity {

    private static final String TAG = "WishlistActivity";

    // UI Components
    private RecyclerView wishlistRv;
    private TextView emptyWishlistTv;
    
    // Adapters & Repositories
    private ProductAdapter productAdapter;
    private WishlistRepository wishlistRepository;
    private CartRepository cartRepository;
    
    // Data
    private List<Product> wishlistProducts;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        wishlistRepository = new WishlistRepository(this);
        cartRepository = new CartRepository(this);

        initViews();
        setupToolbar();
        loadWishlist();
    }

    private void initViews() {
        wishlistRv = findViewById(R.id.wishlistRv);
        emptyWishlistTv = findViewById(R.id.emptyWishlistTv);

        wishlistRv.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, wishlistProducts, userId);
        wishlistRv.setAdapter(productAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Wishlist");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadWishlist() {
        try {
            wishlistProducts = wishlistRepository.getWishlistProducts(userId);
            if (wishlistProducts == null) {
                wishlistProducts = new java.util.ArrayList<>();
            }

            if (wishlistProducts.isEmpty()) {
                wishlistRv.setVisibility(View.GONE);
                emptyWishlistTv.setVisibility(View.VISIBLE);
            } else {
                wishlistRv.setVisibility(View.VISIBLE);
                emptyWishlistTv.setVisibility(View.GONE);
                productAdapter.updateProducts(wishlistProducts);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading wishlist", e);
            Toast.makeText(this, "Error loading wishlist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist(); // Refresh in case user removed items from ProductDetailActivity
    }
}
