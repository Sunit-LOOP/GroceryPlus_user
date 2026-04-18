package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Order;

import java.util.List;

/** RecentOrderAdapter - Displays order summaries in history lists with navigation to tracking details. */
public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.ViewHolder> {

    /** Optional: quick cancel from dashboard (pending/processing orders). */
    public interface OnQuickCancelListener {
        void onQuickCancel(Order order);
    }

    private Context context;
    private List<Order> orders;
    private int userId;
    private OnQuickCancelListener quickCancelListener;

    /** Constructor. */
    public RecentOrderAdapter(Context context, List<Order> orders, int userId) {
        this(context, orders, userId, null);
    }

    public RecentOrderAdapter(Context context, List<Order> orders, int userId, OnQuickCancelListener quickCancelListener) {
        this.context = context;
        this.orders = orders;
        this.userId = userId;
        this.quickCancelListener = quickCancelListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    /** Updates the data source and refreshes the UI. */
    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    /** ViewHolder for mapping order summary components. */
    class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView orderIdTv, orderStatusTv, orderDateTv, orderItemsTv, orderTotalTv;
        MaterialButton quickCancelOrderBtn;
        View recentOrderClickArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            orderItemsTv = itemView.findViewById(R.id.orderItemsTv);
            orderTotalTv = itemView.findViewById(R.id.orderTotalTv);
            quickCancelOrderBtn = itemView.findViewById(R.id.quickCancelOrderBtn);
            recentOrderClickArea = itemView.findViewById(R.id.recentOrderClickArea);

            View trackTarget = recentOrderClickArea != null ? recentOrderClickArea : itemView;
            trackTarget.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && orders != null && pos < orders.size()) {
                    Intent intent = new Intent(context, com.sunit.groceryplus.OrderTrackingActivity.class);
                    intent.putExtra("order_id", orders.get(pos).getOrderId());
                    intent.putExtra("order_status", orders.get(pos).getStatus());
                    intent.putExtra("user_id", userId);
                    context.startActivity(intent);
                }
            });
        }

        /** Binds data and applies status-based color coding. */
        public void bind(Order order) {
            orderIdTv.setText("Order #" + order.getOrderId());
            String statusRaw = order.getStatus();
            String statusShow = statusRaw != null ? statusRaw.trim() : "";
            orderStatusTv.setText(statusShow.isEmpty() ? "—" : statusShow.toUpperCase());
            orderDateTv.setText(order.getOrderDate());
            orderItemsTv.setText(order.getItemCount() + " Items");
            orderTotalTv.setText("Rs. " + String.format("%.2f", order.getTotalAmount()));

            // Simple status color logic for visual distinction
            int statusColor;
            switch (statusShow.toLowerCase()) {
                case "pending": statusColor = context.getResources().getColor(android.R.color.holo_orange_dark); break;
                case "delivered": statusColor = context.getResources().getColor(android.R.color.holo_green_dark); break;
                case "cancelled": statusColor = context.getResources().getColor(android.R.color.holo_red_dark); break;
                default: statusColor = context.getResources().getColor(android.R.color.holo_blue_dark); break;
            }
            orderStatusTv.setTextColor(statusColor);

            boolean canCancel = quickCancelListener != null && !statusShow.isEmpty()
                    && ("pending".equalsIgnoreCase(statusShow) || "processing".equalsIgnoreCase(statusShow));
            if (quickCancelOrderBtn != null) {
                if (canCancel) {
                    quickCancelOrderBtn.setVisibility(View.VISIBLE);
                    quickCancelOrderBtn.setOnClickListener(v -> {
                        if (quickCancelListener != null) {
                            quickCancelListener.onQuickCancel(order);
                        }
                    });
                } else {
                    quickCancelOrderBtn.setVisibility(View.GONE);
                    quickCancelOrderBtn.setOnClickListener(null);
                }
            }
        }
    }
}
