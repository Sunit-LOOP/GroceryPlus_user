package com.sunit.groceryplus;

import android.content.Context;
import com.sunit.groceryplus.models.Review;
import java.util.List;

/** Repository for managing product reviews and ratings in the database. */
public class ReviewRepository {
    // Infrastructure
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public ReviewRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /** Adds a new review for a product by a specific user. */
    public long addReview(int userId, int productId, float rating, String comment) {
        return dbHelper.addReview(userId, productId, rating, comment);
    }

    /** Retrieves all reviews submitted for a specific product. */
    public List<Review> getReviewsForProduct(int productId) {
        return dbHelper.getReviewsForProduct(productId);
    }

    /** Calculates the average rating for a specific product based on all reviews. */
    public float getAverageRatingForProduct(int productId) {
        return dbHelper.getAverageRatingForProduct(productId);
    }
}
