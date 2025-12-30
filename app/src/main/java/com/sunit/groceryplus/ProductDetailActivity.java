package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.adapters.ReviewAdapter;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Review;
import com.sunit.groceryplus.utils.RecentProductsHelper;
import com.sunit.groceryplus.utils.ProductImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    
    private ImageView productImageIv;
    private TextView productNameTv, productPriceTv, productDescriptionTv, productCategoryTv, quantityTv, productVendorTv;
    private TextView productAvgRatingTv, productReviewCountTv, noReviewsTv;
    private RatingBar productAvgRatingBar;
    private ImageButton decreaseBtn, increaseBtn, backBtn, saveForLaterBtn;
    private Button addToCartBtn, writeReviewBtn;
    private RecyclerView productReviewsRv;
    
    private int productId, userId;
    private int quantity = 1;
    
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private ReviewRepository reviewRepository;
    private WishlistRepository wishlistRepository;

    
    private Product product;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getIntExtra("product_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        if (productId == -1 || userId == -1) {
            Toast.makeText(this, "Error loading product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Add to recently viewed
        RecentProductsHelper.addProduct(this, productId);

        productRepository = new ProductRepository(this);
        cartRepository = new CartRepository(this);
        reviewRepository = new ReviewRepository(this);
        wishlistRepository = new WishlistRepository(this);


        initViews();
        setupReviewsRecyclerView();
        loadProductDetails();
        loadReviews();
        setClickListeners();
    }

    private void initViews() {
        productImageIv = findViewById(R.id.productDetailImageIv);
        productNameTv = findViewById(R.id.productDetailNameTv);
        productPriceTv = findViewById(R.id.productDetailPriceTv);
        productDescriptionTv = findViewById(R.id.productDetailDescriptionTv);
        productCategoryTv = findViewById(R.id.productDetailCategoryTv);
        productVendorTv = findViewById(R.id.productDetailVendorTv);
        quantityTv = findViewById(R.id.productDetailQuantityTv);
        decreaseBtn = findViewById(R.id.productDetailDecreaseBtn);
        increaseBtn = findViewById(R.id.productDetailIncreaseBtn);
        backBtn = findViewById(R.id.productDetailBackBtn);
        addToCartBtn = findViewById(R.id.productDetailAddToCartBtn);
        // saveForLaterBtn = findViewById(R.id.productDetailSaveForLaterBtn); // TODO: Add to layout
        
        writeReviewBtn = findViewById(R.id.writeReviewBtn);
        productAvgRatingTv = findViewById(R.id.productAvgRatingTv);
        productReviewCountTv = findViewById(R.id.productReviewCountTv);
        productAvgRatingBar = findViewById(R.id.productAvgRatingBar);
        productReviewsRv = findViewById(R.id.productReviewsRv);
        noReviewsTv = findViewById(R.id.noReviewsTv);
    }

    private void setupReviewsRecyclerView() {
        reviewAdapter = new ReviewAdapter(this, reviewList);
        productReviewsRv.setAdapter(reviewAdapter);
    }

    private void loadProductDetails() {
        try {
            product = productRepository.getProductById(productId);
            if (product != null) {
                productNameTv.setText(product.getProductName());
                productPriceTv.setText("रु " + String.format("%.2f", product.getPrice()));
                productDescriptionTv.setText(product.getDescription());
                productCategoryTv.setText(product.getCategoryName());
                if (product.getVendorName() != null) {
                    productVendorTv.setText("Sold by: " + product.getVendorName());
                    productVendorTv.setVisibility(View.VISIBLE);
                } else {
                    productVendorTv.setVisibility(View.GONE);
                }
                quantityTv.setText(String.valueOf(quantity));

                ProductImageLoader.load(this, productImageIv, product.getImage(), R.drawable.product_icon);

                // Update save for later button icon
                updateSaveForLaterIcon();
            } else {
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading product", e);
            finish();
        }
    }

    private void loadReviews() {
        loadReviewsFromDatabase();
    }

    private void loadReviewsFromDatabase() {
        try {
            reviewList = reviewRepository.getReviewsForProduct(productId);
            float avgRating = reviewRepository.getAverageRatingForProduct(productId);

            productAvgRatingTv.setText(String.format("%.1f", avgRating));
            productAvgRatingBar.setRating(avgRating);
            productReviewCountTv.setText("Based on " + reviewList.size() + " reviews");

            if (reviewList.isEmpty()) {
                noReviewsTv.setVisibility(View.VISIBLE);
                productReviewsRv.setVisibility(View.GONE);
            } else {
                noReviewsTv.setVisibility(View.GONE);
                productReviewsRv.setVisibility(View.VISIBLE);
                reviewAdapter.updateReviews(reviewList);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading reviews from database", e);
        }
    }

    private void setClickListeners() {
        decreaseBtn.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTv.setText(String.valueOf(quantity));
            }
        });

        increaseBtn.setOnClickListener(v -> {
            quantity++;
            quantityTv.setText(String.valueOf(quantity));
        });

        addToCartBtn.setOnClickListener(v -> addToCart());
        
        writeReviewBtn.setOnClickListener(v -> showWriteReviewDialog());

        backBtn.setOnClickListener(v -> finish());
    }

    private void showWriteReviewDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_write_review, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        RatingBar ratingBar = view.findViewById(R.id.dialogRatingBar);
        TextInputEditText reviewEt = view.findViewById(R.id.dialogReviewEt);
        Button cancelBtn = view.findViewById(R.id.dialogCancelBtn);
        Button submitBtn = view.findViewById(R.id.dialogSubmitBtn);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        submitBtn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = reviewEt.getText().toString().trim();

            if (comment.isEmpty()) {
                reviewEt.setError("Please write a comment");
                return;
            }

            long result = reviewRepository.addReview(userId, productId, rating, comment);
            if (result != -1) {
                Toast.makeText(ProductDetailActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                loadReviews();
                dialog.dismiss();
            } else {
                Toast.makeText(ProductDetailActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void addToCart() {
        if (cartRepository.addToCart(userId, productId, quantity)) {
            Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleSaveForLater() {
        if (wishlistRepository.isInWishlist(userId, productId)) {
            wishlistRepository.removeFromWishlist(userId, productId);
            Toast.makeText(ProductDetailActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
        } else {
            wishlistRepository.addToWishlist(userId, productId);
            Toast.makeText(ProductDetailActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
        }
        updateSaveForLaterIcon();
    }

    private void updateSaveForLaterIcon() {
        // TODO: Uncomment when saveForLaterBtn is added to layout
        if (wishlistRepository.isInWishlist(userId, productId)) {
            // saveForLaterBtn.setImageResource(R.drawable.ic_save_for_later_filled);
        } else {
            // saveForLaterBtn.setImageResource(R.drawable.ic_save_for_later);
        }
    }
}
