package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Order;

import java.util.List;

/**
 * Adapter for displaying a user's recent orders in their Order History.
 * Shows order summary including ID, Status, Date, total items, and cost.
 * Click navigates to OrderTrackingActivity for full details.
 */
public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orders;
    private int userId;

    public RecentOrderAdapter(Context context, List<Order> orders, int userId) {
        this.context = context;
        this.orders = orders;
        this.userId = userId;
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

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTv, orderStatusTv, orderDateTv, orderItemsTv, orderTotalTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            orderItemsTv = itemView.findViewById(R.id.orderItemsTv);
            orderTotalTv = itemView.findViewById(R.id.orderTotalTv);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, com.sunit.groceryplus.OrderTrackingActivity.class);
                    intent.putExtra("order_id", orders.get(pos).getOrderId());
                    intent.putExtra("order_status", orders.get(pos).getStatus());
                    intent.putExtra("user_id", userId);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Order order) {
            orderIdTv.setText("Order #" + order.getOrderId());
            orderStatusTv.setText(order.getStatus().toUpperCase());
            orderDateTv.setText(order.getOrderDate());
            orderItemsTv.setText(order.getItemCount() + " Items");
            orderTotalTv.setText("Rs. " + String.format("%.2f", order.getTotalAmount()));

            // Simple status color logic for visual distinction
            int statusColor;
            switch (order.getStatus().toLowerCase()) {
                case "pending": statusColor = context.getResources().getColor(android.R.color.holo_orange_dark); break;
                case "delivered": statusColor = context.getResources().getColor(android.R.color.holo_green_dark); break;
                case "cancelled": statusColor = context.getResources().getColor(android.R.color.holo_red_dark); break;
                default: statusColor = context.getResources().getColor(android.R.color.holo_blue_dark); break;
            }
            orderStatusTv.setTextColor(statusColor);
        }
    }
}
