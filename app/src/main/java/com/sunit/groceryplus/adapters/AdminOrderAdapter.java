package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Order;

import java.util.List;

/** AdminOrderAdapter - Manages customer orders in the Admin Panel (Status updates, transitions, delivery assignment). */
public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderActionListener listener;

    /** Interface for Admin Order actions (Status transitions and delivery personnel assignment). */
    public interface OnOrderActionListener {
        void onUpdateStatusClick(Order order);
        void onAssignDeliveryClick(Order order);
    }

    /** Constructor. */
    public AdminOrderAdapter(Context context, List<Order> orders, OnOrderActionListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
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

    class OrderViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView orderIdTv, orderDateTv, orderAmountTv, orderStatusTv;
        TextView paymentStatusTv, deliveryPersonTv, deliveryFeeTv;
        Button updateStatusBtn, assignDeliveryBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            orderAmountTv = itemView.findViewById(R.id.orderAmountTv);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTv);
            paymentStatusTv = itemView.findViewById(R.id.paymentStatusTv);
            deliveryPersonTv = itemView.findViewById(R.id.deliveryPersonTv);
            deliveryFeeTv = itemView.findViewById(R.id.deliveryFeeTv);
            updateStatusBtn = itemView.findViewById(R.id.updateStatusBtn);
            assignDeliveryBtn = itemView.findViewById(R.id.assignDeliveryBtn);

            updateStatusBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUpdateStatusClick(orders.get(position));
                }
            });

            assignDeliveryBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAssignDeliveryClick(orders.get(position));
                }
            });
        }

        /** Binds order data and applies color coding. */
        public void bind(Order order) {
            orderIdTv.setText("Order #" + order.getOrderId());
            orderDateTv.setText(order.getOrderDate()); // Assuming Date format is string
            orderAmountTv.setText("Rs. " + String.format("%.2f", order.getTotalAmount()));
            if (deliveryFeeTv != null) {
                deliveryFeeTv.setText("(Fee: Rs. " + String.format("%.2f", order.getDeliveryFee()) + ")");
            }
            orderStatusTv.setText(order.getStatus());

            // Status Color Coding for better visualization
            String status = order.getStatus();
            if ("Pending".equalsIgnoreCase(status)) {
                orderStatusTv.setTextColor(Color.parseColor("#FF9800")); // Orange
            } else if ("Processing".equalsIgnoreCase(status)) {
                orderStatusTv.setTextColor(Color.parseColor("#2196F3")); // Blue
            } else if ("Shipped".equalsIgnoreCase(status)) {
                orderStatusTv.setTextColor(Color.parseColor("#9C27B0")); // Purple
            } else if ("Delivered".equalsIgnoreCase(status)) {
                orderStatusTv.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else if ("Cancelled".equalsIgnoreCase(status)) {
                orderStatusTv.setTextColor(Color.parseColor("#F44336")); // Red
            } else {
                orderStatusTv.setTextColor(Color.DKGRAY);
            }

            // Payment Status Color Coding
            String paymentMethod = order.getPaymentMethod();
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                paymentStatusTv.setText(String.format("Received (%s)", paymentMethod));
                paymentStatusTv.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else if (order.isPaymentReceived()) {
                paymentStatusTv.setText("Received");
                paymentStatusTv.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                paymentStatusTv.setText("Not Received");
                paymentStatusTv.setTextColor(Color.parseColor("#F44336")); // Red
            }
            
            // Show Delivery Assignment Info
            if (order.getDeliveryPersonName() != null) {
                deliveryPersonTv.setText(order.getDeliveryPersonName());
            } else {
                deliveryPersonTv.setText("Unassigned");
            }
        }
    }
}
