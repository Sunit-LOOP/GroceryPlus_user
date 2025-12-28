package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Promotion;

import java.util.List;

public class AdminPromotionAdapter extends RecyclerView.Adapter<AdminPromotionAdapter.ViewHolder> {

    private Context context;
    private List<Promotion> promotions;
    private OnPromotionActionListener listener;

    public interface OnPromotionActionListener {
        void onDeleteClick(Promotion promotion);
    }

    public AdminPromotionAdapter(Context context, List<Promotion> promotions, OnPromotionActionListener listener) {
        this.context = context;
        this.promotions = promotions;
        this.listener = listener;
    }

    public void updatePromotions(List<Promotion> newPromotions) {
        this.promotions = newPromotions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_promotion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Promotion promotion = promotions.get(position);
        holder.codeTv.setText(promotion.getCode());
        holder.discountTv.setText(String.format("%.0f%% Discount", promotion.getDiscountPercentage()));
        holder.validUntilTv.setText("Valid until: " + promotion.getValidUntil());

        holder.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(promotion));
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView codeTv, discountTv, validUntilTv;
        ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeTv = itemView.findViewById(R.id.promoCodeTv);
            discountTv = itemView.findViewById(R.id.promoDiscountTv);
            validUntilTv = itemView.findViewById(R.id.promoValidUntilTv);
            deleteBtn = itemView.findViewById(R.id.deletePromoBtn);
        }
    }
}
