package com.sunit.groceryplus;

import android.content.Context;
import com.sunit.groceryplus.models.Review;
import java.util.List;

public class ReviewRepository {
    private DatabaseHelper dbHelper;

    public ReviewRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addReview(int userId, int productId, float rating, String comment) {
        return dbHelper.addReview(userId, productId, rating, comment);
    }

    public List<Review> getReviewsForProduct(int productId) {
        return dbHelper.getReviewsForProduct(productId);
    }

    public float getAverageRatingForProduct(int productId) {
        return dbHelper.getAverageRatingForProduct(productId);
    }
}
