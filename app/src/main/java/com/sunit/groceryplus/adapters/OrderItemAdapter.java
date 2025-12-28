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

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;

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

    public void updateOrderItems(List<OrderItem> items) {
        this.orderItems = items;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView quantityTextView;
        TextView unitPriceTextView;
        TextView subtotalTextView;

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
            unitPriceTextView.setText("रु " + String.format("%.2f", item.getPrice()));
            subtotalTextView.setText("रु " + String.format("%.2f", item.getSubtotal()));

            // Set product image with improved handling
            String imageName = item.getImage();
            if (imageName != null && !imageName.isEmpty()) {
                int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
                if (resourceId != 0) {
                    productImageView.setImageResource(resourceId);
                } else {
                    // Try to assign specific images based on product name
                    int specificImage = getSpecificImageForProduct(item.getProductName());
                    productImageView.setImageResource(specificImage);
                }
            } else {
                // Assign specific images based on product name
                int specificImage = getSpecificImageForProduct(item.getProductName());
                productImageView.setImageResource(specificImage);
            }
        }

        /**
         * Get specific image resource based on product name
         */
        private int getSpecificImageForProduct(String productName) {
            if (productName == null) {
                return R.drawable.product_icon;
            }
            
            String lowerName = productName.toLowerCase();
            
            // Match specific products to images
            if (lowerName.contains("milk") || lowerName.contains("dairy")) {
                return R.drawable.bottle_milk;
            } else if (lowerName.contains("cheese")) {
                return R.drawable.cheese_slice;
            } else if (lowerName.contains("curd") || lowerName.contains("yogurt") || lowerName.contains("dahi")) {
                return R.drawable.curd;
            } else if (lowerName.contains("tomato")) {
                return R.drawable.tomato_red;
            } else if (lowerName.contains("cabbage")) {
                return R.drawable.cabbage;
            } else if (lowerName.contains("cauliflower")) {
                return R.drawable.cauliflower;
            } else if (lowerName.contains("lettuce") || lowerName.contains("leaf")) {
                return R.drawable.lettuce_leaf;
            } else if (lowerName.contains("paneer")) {
                return R.drawable.paneer_cubes;
            } else if (lowerName.contains("bottle")) {
                return R.drawable.bottle_gourd;
            } else if (lowerName.contains("green") && (lowerName.contains("vegetable") || lowerName.contains("leaf"))) {
                return R.drawable.green_vegetable;
            } else {
                // Default product image
                return R.drawable.product_icon;
            }
        }
    }
}