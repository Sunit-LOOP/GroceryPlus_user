package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** OrderAdapter - Displays user's order history with status tracking and delivery countdowns. */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderClickListener listener;

    /** Interface for order interactions (View, Reorder, Cancel). */
    public interface OnOrderClickListener {
        void onOrderClick(Order order);     // View details
        void onReorderClick(Order order);   // Add items back to cart
        void onCancelOrderClick(Order order); // Cancel pending order
    }

    /** Extended interface to include Refund capability. */
    public interface OnOrderActionExtendedListener extends OnOrderClickListener {
        void onRefundClick(Order order);    // Request refund
    }

    /** Constructor. */
    public OrderAdapter(Context context, List<Order> orders, OnOrderClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
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

    /** ViewHolder for Order items, including realtime delivery timer support. */
    class OrderViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView orderIdTv, orderDateTv;
        private TextView orderStatusTv, orderTotalTv, deliveryFeeTv;
        private TextView orderItemCountTv, orderTimerTv;
        
        // Timer Logic for Shipped -> Delivered Countdown
        private Handler timerHandler = new Handler();
        private Runnable timerRunnable;
        
        View reorderBtn;
        View cancelBtn;
        View refundBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            orderStatusTv = itemView.findViewById(R.id.orderStatusTv);
            orderTotalTv = itemView.findViewById(R.id.orderTotalTv);
            deliveryFeeTv = itemView.findViewById(R.id.deliveryFeeTv);
            orderItemCountTv = itemView.findViewById(R.id.orderItemCountTv);
            orderTimerTv = itemView.findViewById(R.id.orderTimerTv);
            reorderBtn = itemView.findViewById(R.id.reorderBtn);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
            refundBtn = itemView.findViewById(R.id.refundBtn);

            reorderBtn.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReorderClick(orders.get(position));
                }
            });

            if (cancelBtn != null) {
                cancelBtn.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onCancelOrderClick(orders.get(position));
                    }
                });
            }

            if (refundBtn != null) {
                refundBtn.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        if (listener instanceof OnOrderActionExtendedListener) {
                            ((OnOrderActionExtendedListener) listener).onRefundClick(orders.get(position));
                        }
                    }
                });
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOrderClick(orders.get(position));
                }
            });
        }

        /** Binds data and initializes button visibility. */
        public void bind(Order order) {
            orderIdTv.setText("Order #" + order.getOrderId());
            orderDateTv.setText(order.getOrderDate());
            orderStatusTv.setText(order.getStatus().toUpperCase());
            orderTotalTv.setText("Rs. " + String.format("%.2f", order.getTotalAmount()));
            if (deliveryFeeTv != null) {
                deliveryFeeTv.setText("(Fee: Rs. " + String.format("%.2f", order.getDeliveryFee()) + ")");
            }
            
            if (orderItemCountTv != null) {
                orderItemCountTv.setText(order.getItemCount() + " items");
            }

            // Set status color
            int statusColor = getStatusColor(order.getStatus());
            orderStatusTv.setTextColor(statusColor);

            // Reorder button always visible
            reorderBtn.setVisibility(View.VISIBLE);

            // Cancel button visibility - Available for Pending or Processing
            if (cancelBtn != null) {
                if ("pending".equalsIgnoreCase(order.getStatus()) || "processing".equalsIgnoreCase(order.getStatus())) {
                    cancelBtn.setVisibility(View.VISIBLE);
                } else {
                    cancelBtn.setVisibility(View.GONE);
                }
            }

            // Refund button visibility - Available for Delivered
            if (refundBtn != null) {
                if ("delivered".equalsIgnoreCase(order.getStatus())) {
                    refundBtn.setVisibility(View.VISIBLE);
                } else {
                    refundBtn.setVisibility(View.GONE);
                }
            }

            setupTimer(order);
        }

        /** Configures a 30-minute estimated delivery countdown if order is Shipped. */
        private void setupTimer(final Order order) {
            if (timerRunnable != null) {
                timerHandler.removeCallbacks(timerRunnable);
            }

            if ("shipped".equalsIgnoreCase(order.getStatus()) && order.getShippedDate() != null) {
                orderTimerTv.setVisibility(View.VISIBLE);
                
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date shippedDate = sdf.parse(order.getShippedDate());
                            if (shippedDate == null) return;
                            
                            long currentTime = System.currentTimeMillis();
                            long elapsedMillis = currentTime - shippedDate.getTime();
                            long thirtyMinutesMillis = 30 * 60 * 1000;
                            long remainingMillis = thirtyMinutesMillis - elapsedMillis;

                            if (remainingMillis > 0) {
                                long minutes = (remainingMillis / 1000) / 60;
                                long seconds = (remainingMillis / 1000) % 60;
                                orderTimerTv.setText(String.format("Estimated Delivery: %02d:%02d", minutes, seconds));
                                orderTimerTv.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                                timerHandler.postDelayed(this, 1000);
                            } else {
                                orderTimerTv.setText("Estimated Delivery: Arriving any moment");
                                orderTimerTv.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                            }
                        } catch (Exception e) {
                            orderTimerTv.setVisibility(View.GONE);
                        }
                    }
                };
                timerHandler.post(timerRunnable);
            } else {
                orderTimerTv.setVisibility(View.GONE);
            }
        }

        /** Resolves color resource based on current status string. */
        private int getStatusColor(String status) {
            if (status == null) return context.getResources().getColor(android.R.color.black);
            switch (status.toLowerCase()) {
                case "pending":
                    return context.getResources().getColor(android.R.color.holo_orange_dark);
                case "processing":
                    return context.getResources().getColor(android.R.color.holo_blue_dark);
                case "shipped":
                    return context.getResources().getColor(android.R.color.holo_purple);
                case "delivered":
                    return context.getResources().getColor(android.R.color.holo_green_dark);
                case "cancelled":
                    return context.getResources().getColor(android.R.color.holo_red_dark);
                case "refunded":
                    return context.getResources().getColor(android.R.color.darker_gray);
                default:
                    return context.getResources().getColor(android.R.color.black);
            }
        }
    }
}
