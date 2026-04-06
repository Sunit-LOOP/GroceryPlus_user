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
import com.sunit.groceryplus.models.OrderItem;
import com.sunit.groceryplus.utils.ProductImageLoader;
import com.sunit.groceryplus.utils.RealDeviceImageSystem;

import java.util.List;

/** OrderItemAdapter - Displays individual products within an order recap or details screen. */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;

    /** Constructor. */
    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    /** Updates the data source and refreshes the UI. */
    public void updateOrderItems(List<OrderItem> items) {
        this.orderItems = items;
        notifyDataSetChanged();
    }

    /** ViewHolder for Order Items. */
    class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        ImageView productImageView;
        TextView productNameTextView, quantityTextView, unitPriceTextView, subtotalTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            unitPriceTextView = itemView.findViewById(R.id.unitPriceTextView);
            subtotalTextView = itemView.findViewById(R.id.subtotalTextView);
        }

        public void bind(OrderItem item) {
            productNameTextView.setText(item.getProductName());
            quantityTextView.setText("Qty: " + item.getQuantity());
            unitPriceTextView.setText("Rs. " + String.format("%.2f", item.getPrice()));
            subtotalTextView.setText("Rs. " + String.format("%.2f", item.getSubtotal()));

            // Simplified Image Loading using Central System
            RealDeviceImageSystem.loadProductImage(context, productImageView, item.getImage(), item.getProductName());
        }


    }
}