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


/** WishlistActivity - Grid interface for managing products saved by the user for later purchase. */
public class WishlistActivity extends AppCompatActivity {

    // Infrastructure & UI
    private static final String TAG = "WishlistActivity";
    private RecyclerView wishlistRv;
    private View emptyWishlistTv;

    // Adapters & Data
    private ProductAdapter productAdapter;
    private WishlistRepository wishlistRepository;
    private CartRepository cartRepository;
    private List<Product> wishlistProducts;
    private int userId;

    /** Initializes the activity, verifies user session, and loads wishlist data. */
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

    /** Links UI components to functional fields and configures the grid layout. */
    private void initViews() {
        wishlistRv = findViewById(R.id.wishlistRv);
        emptyWishlistTv = findViewById(R.id.emptyWishlistTv);

        wishlistRv.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, wishlistProducts, userId);
        wishlistRv.setAdapter(productAdapter);
    }

    /** Configures the toolbar with back navigation. */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Wishlist");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /** Fetches the latest wishlist products for the current user. */
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
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Refreshes the wishlist when the activity resumes. */
    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist(); // Refresh in case user removed items from ProductDetailActivity
    }
}
