package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminReviewAdapter;
import com.sunit.groceryplus.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsManagementActivity extends AppCompatActivity {

    private RecyclerView reviewsRv;
    private AdminReviewAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        
        reviewsRv = findViewById(R.id.reviewsRv);

        setupRecyclerView();
        loadReviews();
    }

    private void setupRecyclerView() {
        reviewsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminReviewAdapter(this, new ArrayList<>(), new AdminReviewAdapter.OnReviewActionListener() {
            @Override
            public void onDeleteClick(Review review) {
                showDeleteConfirmationDialog(review);
            }
        });
        reviewsRv.setAdapter(adapter);
    }

    private void loadReviews() {
        List<Review> reviews = new ArrayList<>();
        Cursor cursor = dbHelper.getAllReviews();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_REVIEW_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_USER_ID));
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_PRODUCT_ID));
                int rating = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_RATING));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_COMMENT));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ReviewEntry.COLUMN_NAME_CREATED_AT));
                
                // Joined columns
                String productName = "";
                int prodNameIdx = cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_NAME_PRODUCT_NAME);
                if (prodNameIdx != -1) productName = cursor.getString(prodNameIdx);
                
                String userName = "";
                int userNameIdx = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_NAME_USER_NAME);
                if (userNameIdx != -1) userName = cursor.getString(userNameIdx);

                reviews.add(new Review(id, userId, userName, productId, productName, rating, comment, createdAt));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        adapter.updateReviews(reviews);
    }

    private void showDeleteConfirmationDialog(Review review) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteReview(review.getReviewId())) {
                        Toast.makeText(this, "Review deleted", Toast.LENGTH_SHORT).show();
                        loadReviews();
                    } else {
                        Toast.makeText(this, "Error deleting review", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
