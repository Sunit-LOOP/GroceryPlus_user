package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.CartItem;
import com.sunit.groceryplus.utils.RealDeviceImageSystem;

import java.util.List;

/** CartAdapter - Manages and displays items in the Shopping Cart, including quantity updates and subtotal calculations. */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnQuantityChangeListener quantityChangeListener;

    /** Interface for listening to quantity changes to update UI in real-time. */
    public interface OnQuantityChangeListener {
        void onQuantityChanged(int cartItemId, int newQuantity);
        void onItemRemoved(int cartItemId);
    }

    /** Constructor. */
    public CartAdapter(Context context, List<CartItem> cartItems, OnQuantityChangeListener quantityChangeListener) {
        this.context = context;
        this.cartItems = cartItems;
        this.quantityChangeListener = quantityChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    /** Updates the list of cart items and refreshes view. */
    public void updateCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    /** Calculates the current total price of all items in the cart. */
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    /** Calculates the total price including delivery fees based on thresholds. */
    public double getTotalPriceWithDelivery() {
        double subtotal = getTotalPrice();
        if (subtotal >= com.sunit.groceryplus.utils.PaymentConfig.FREE_DELIVERY_THRESHOLD) {
            return subtotal;
        }
        return subtotal + com.sunit.groceryplus.utils.PaymentConfig.DELIVERY_FEE;
    }

    /** ViewHolder for Cart Items. */
    class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView productNameTv, priceTv, quantityTv, subtotalTv;
        ImageButton increaseBtn, decreaseBtn, removeBtn;
        ImageView productImageIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Corrected view IDs to match the actual layout
            productNameTv = itemView.findViewById(R.id.cartProductNameTv);
            priceTv = itemView.findViewById(R.id.cartProductPriceTv);
            quantityTv = itemView.findViewById(R.id.cartQuantityTv);
            subtotalTv = itemView.findViewById(R.id.cartSubtotalTv);
            increaseBtn = itemView.findViewById(R.id.increaseQuantityBtn);
            decreaseBtn = itemView.findViewById(R.id.decreaseQuantityBtn);
            removeBtn = itemView.findViewById(R.id.removeCartItemBtn);
            productImageIv = itemView.findViewById(R.id.cartProductImageIv);
        }

        /** Binds data and handles quantity interactions. */
        public void bind(CartItem item) {
            productNameTv.setText(item.getProductName());
            priceTv.setText("Rs. " + String.format("%.2f", item.getPrice()));
            quantityTv.setText(String.valueOf(item.getQuantity()));
            subtotalTv.setText("Subtotal: Rs. " + String.format("%.2f", item.getSubtotal()));

            // Set product image with RealDeviceImageSystem for guaranteed display
            String imageName = item.getImage();
            String productName = item.getProductName();
            RealDeviceImageSystem.loadProductImage(context, productImageIv, imageName, productName);

            // Handle Quantity Increase
            increaseBtn.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(item.getCartId(), newQuantity);
                }
            });

            // Handle Quantity Decrease
            decreaseBtn.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity > 0) {
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged(item.getCartId(), newQuantity);
                    }
                } else if (newQuantity == 0) {
                    if (quantityChangeListener != null) {
                        // If quantity reaches 0, treat it as removal
                        quantityChangeListener.onItemRemoved(item.getCartId());
                    }
                }
            });

            // Handle Explicit Remove Button
            removeBtn.setOnClickListener(v -> {
                if (quantityChangeListener != null) {
                    quantityChangeListener.onItemRemoved(item.getCartId());
                }
            });
        }
    }
}