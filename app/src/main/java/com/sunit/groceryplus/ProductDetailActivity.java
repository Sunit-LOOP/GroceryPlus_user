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
import com.sunit.groceryplus.network.ApiService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    
    private ImageView productImageIv;
    private TextView productNameTv, productPriceTv, productDescriptionTv, productCategoryTv, quantityTv, productVendorTv;
    private TextView productAvgRatingTv, productReviewCountTv, noReviewsTv;
    private RatingBar productAvgRatingBar;
    private ImageButton decreaseBtn, increaseBtn, backBtn;
    private Button addToCartBtn, writeReviewBtn;
    private RecyclerView productReviewsRv;
    
    private int productId, userId;
    private int quantity = 1;
    
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private ReviewRepository reviewRepository;
    private ApiService apiService;
    
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

        productRepository = new ProductRepository(this);
        cartRepository = new CartRepository(this);
        reviewRepository = new ReviewRepository(this);
        apiService = new ApiService(this);

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

                int imageResource = getImageResource(product.getImage());
                productImageIv.setImageResource(imageResource != 0 ? imageResource : R.drawable.product_icon);
            } else {
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading product", e);
            finish();
        }
    }

    private void loadReviews() {
        // Try API first, fallback to database
        apiService.getProductReviews(productId, new ApiService.ApiCallback<java.util.List<org.json.JSONObject>>() {
            @Override
            public void onSuccess(java.util.List<JSONObject> response) {
                try {
                    reviewList.clear();
                    float totalRating = 0;
                    for (JSONObject reviewJson : response) {
                        int reviewId = reviewJson.optInt("review_id", 0);
                        int userIdFromJson = reviewJson.optInt("user_id", 0);
                        String userName = reviewJson.optString("user_name", "Anonymous");
                        int productIdFromJson = reviewJson.optInt("product_id", 0);
                        String productName = ""; // API might not provide this
                        int rating = (int) reviewJson.optDouble("rating", 0);
                        String comment = reviewJson.optString("comment", "");
                        String createdAt = reviewJson.optString("review_date", "");

                        Review review = new Review(reviewId, userIdFromJson, userName, productIdFromJson, productName, rating, comment, createdAt);
                        reviewList.add(review);
                        totalRating += review.getRating();
                    }

                    float avgRating = reviewList.isEmpty() ? 0 : totalRating / reviewList.size();
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
                    Log.d(TAG, "Loaded reviews from API");
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing reviews from API", e);
                    loadReviewsFromDatabase();
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API reviews failed: " + error + " - Loading from database");
                loadReviewsFromDatabase();
            }
        });
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

            // Try API first, fallback to database
            java.util.Map<String, Object> reviewData = new java.util.HashMap<>();
            reviewData.put("product_id", productId);
            reviewData.put("rating", rating);
            reviewData.put("comment", comment);

            apiService.submitReview(reviewData, new ApiService.ApiCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(ProductDetailActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                    loadReviews();
                    dialog.dismiss();
                    Log.d(TAG, "Review submitted via API");
                }

                @Override
                public void onError(String error) {
                    Log.d(TAG, "API submit review failed: " + error + " - Using database");
                    long result = reviewRepository.addReview(userId, productId, rating, comment);
                    if (result != -1) {
                        Toast.makeText(ProductDetailActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        loadReviews();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private void addToCart() {
        // Try API first, fallback to database
        apiService.addToCart(productId, quantity, new ApiService.ApiCallback<org.json.JSONObject>() {
            @Override
            public void onSuccess(org.json.JSONObject response) {
                Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Product added to cart via API");
                finish();
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "API add to cart failed: " + error + " - Using database");
                if (cartRepository.addToCart(userId, productId, quantity)) {
                    Toast.makeText(ProductDetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getImageResource(String imageName) {
        if (imageName == null || imageName.isEmpty()) return R.drawable.product_icon;
        try {
            return getResources().getIdentifier(imageName, "drawable", getPackageName());
        } catch (Exception e) {
            return R.drawable.product_icon;
        }
    }
}
