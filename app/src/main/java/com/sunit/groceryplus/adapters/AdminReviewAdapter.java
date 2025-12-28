package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Review;

import java.util.List;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviews;
    private OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onDeleteClick(Review review);
    }

    public AdminReviewAdapter(Context context, List<Review> reviews, OnReviewActionListener listener) {
        this.context = context;
        this.reviews = reviews;
        this.listener = listener;
    }

    public void updateReviews(List<Review> newReviews) {
        this.reviews = newReviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.productTv.setText(review.getProductName());
        holder.ratingBar.setRating(review.getRating());
        holder.commentTv.setText(review.getComment());
        holder.userTv.setText("By: " + review.getUserName() + " - " + review.getCreatedAt());

        holder.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(review));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTv, commentTv, userTv;
        RatingBar ratingBar;
        ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTv = itemView.findViewById(R.id.reviewProductTv);
            commentTv = itemView.findViewById(R.id.reviewCommentTv);
            userTv = itemView.findViewById(R.id.reviewUserTv);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
            deleteBtn = itemView.findViewById(R.id.deleteReviewBtn);
        }
    }
}
