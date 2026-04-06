package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.OrderItem;

import java.util.List;

/** OrderDetailAdapter - Manages the display and actions for individual items within an order. */
public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> items;
    private String orderStatus;
    private boolean isPacked;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onCancelItem(OrderItem item);
        void onReportIssue(OrderItem item);
        void onRequestRefund(OrderItem item);
    }

    public OrderDetailAdapter(Context context, List<OrderItem> items, String orderStatus, boolean isPacked, OnItemActionListener listener) {
        this.context = context;
        this.items = items;
        this.orderStatus = orderStatus;
        this.isPacked = isPacked;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.productNameTv.setText(item.getProductName());
        holder.quantityPriceTv.setText(item.getQuantity() + " x Rs. " + String.format("%.2f", item.getPrice()));
        holder.subtotalTv.setText("Rs. " + String.format("%.2f", item.getSubtotal()));
        
        holder.itemStatusTv.setText("Status: " + (item.getItemStatus() != null ? item.getItemStatus() : "active"));

        // Logic for buttons
        boolean isDelivered = "delivered".equalsIgnoreCase(orderStatus);
        boolean isPending = "pending".equalsIgnoreCase(orderStatus);
        boolean isCancelledItem = "cancelled".equalsIgnoreCase(item.getItemStatus());

        if (isCancelledItem) {
            holder.itemStatusTv.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.itemActionsLayout.setVisibility(View.GONE);
            holder.refundInfoTv.setVisibility(View.VISIBLE);
            holder.refundInfoTv.setText("Refund: " + item.getRefundStatus() + " (Rs. " + String.format("%.2f", item.getRefundAmount()) + ")");
        } else {
            holder.itemStatusTv.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.itemActionsLayout.setVisibility(View.VISIBLE);
            holder.refundInfoTv.setVisibility(View.GONE);

            // Cancel button: only if order is pending and not packed
            if (isPending && !isPacked) {
                holder.cancelItemBtn.setVisibility(View.VISIBLE);
            } else {
                holder.cancelItemBtn.setVisibility(View.GONE);
            }

            // Report issue: only if order is delivered
            if (isDelivered) {
                holder.reportIssueBtn.setVisibility(View.VISIBLE);
                holder.requestRefundBtn.setVisibility(View.VISIBLE);
            } else {
                holder.reportIssueBtn.setVisibility(View.GONE);
                holder.requestRefundBtn.setVisibility(View.GONE);
            }

            // If already refund requested, hide buttons and show status
            if ("refund_requested".equalsIgnoreCase(item.getItemStatus())) {
                holder.itemActionsLayout.setVisibility(View.GONE);
                holder.refundInfoTv.setVisibility(View.VISIBLE);
                holder.refundInfoTv.setText("Refund: " + item.getRefundStatus() + " (Rs. " + String.format("%.2f", item.getSubtotal()) + ")");
            }
        }

        holder.cancelItemBtn.setOnClickListener(v -> listener.onCancelItem(item));
        holder.reportIssueBtn.setOnClickListener(v -> listener.onReportIssue(item));
        holder.requestRefundBtn.setOnClickListener(v -> listener.onRequestRefund(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productIv;
        TextView productNameTv, quantityPriceTv, itemStatusTv, subtotalTv, refundInfoTv;
        MaterialButton cancelItemBtn, reportIssueBtn, requestRefundBtn;
        View itemActionsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productIv = itemView.findViewById(R.id.productIv);
            productNameTv = itemView.findViewById(R.id.productNameTv);
            quantityPriceTv = itemView.findViewById(R.id.quantityPriceTv);
            itemStatusTv = itemView.findViewById(R.id.itemStatusTv);
            subtotalTv = itemView.findViewById(R.id.subtotalTv);
            refundInfoTv = itemView.findViewById(R.id.refundInfoTv);
            cancelItemBtn = itemView.findViewById(R.id.cancelItemBtn);
            reportIssueBtn = itemView.findViewById(R.id.reportIssueBtn);
            requestRefundBtn = itemView.findViewById(R.id.requestRefundBtn);
            itemActionsLayout = itemView.findViewById(R.id.itemActionsLayout);
        }
    }
}
