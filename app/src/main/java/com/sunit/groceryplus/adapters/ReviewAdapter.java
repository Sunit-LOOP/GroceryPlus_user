package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.userNameTv.setText(review.getUserName());
        holder.dateTv.setText(review.getCreatedAt());
        holder.ratingBar.setRating(review.getRating());
        holder.commentTv.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTv, dateTv, commentTv;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.reviewUserNameTv);
            dateTv = itemView.findViewById(R.id.reviewDateTv);
            commentTv = itemView.findViewById(R.id.reviewCommentTv);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
        }
    }
}
