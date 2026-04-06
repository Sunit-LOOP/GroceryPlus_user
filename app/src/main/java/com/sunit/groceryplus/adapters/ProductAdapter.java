package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sunit.groceryplus.ProductDetailActivity;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.utils.ProductImageLoader;
import com.sunit.groceryplus.utils.RealDeviceImageSystem;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private int userId;
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public void setCartUpdateListener(OnCartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }

    public ProductAdapter(Context context, List<Product> productList, int userId) {
        this.context = context;
        this.productList = productList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_card_modern, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productCategory, productVendor, productPrice, stockBadge, productRatingBadge;
        ToggleButton favoriteBtn;
        MaterialButton addToCartBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productVendor = itemView.findViewById(R.id.productVendor);
            productPrice = itemView.findViewById(R.id.productPrice);
            stockBadge = itemView.findViewById(R.id.stockBadge);
            productRatingBadge = itemView.findViewById(R.id.productRatingBadge);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Product product = productList.get(pos);
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("product_id", product.getProductId());
                    intent.putExtra("user_id", userId);
                    context.startActivity(intent);
                }
            });

            // Actual Add to Cart click listener
            if (addToCartBtn != null) {
                addToCartBtn.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Product product = productList.get(pos);
                        com.sunit.groceryplus.CartRepository cartRepo = new com.sunit.groceryplus.CartRepository(context);
                        if (cartRepo.addToCart(userId, product.getProductId(), 1)) {
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                            // Trigger the listener if set (e.g., to update cart badge in activity)
                            if (cartUpdateListener != null) {
                                cartUpdateListener.onCartUpdated();
                            }
                        } else {
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        
        public void bind(Product product) {
            productName.setText(product.getProductName());
            productCategory.setText(product.getCategoryName());
            productVendor.setText(product.getVendorName());
            
            productPrice.setText(String.format("Rs. %.2f", product.getPrice()));

            if (product.getRating() > 0) {
                productRatingBadge.setText(String.format("%.1f ★", product.getRating()));
                productRatingBadge.setVisibility(View.VISIBLE);
            } else {
                productRatingBadge.setVisibility(View.GONE);
            }

            if (product.getStockQuantity() <= 0) {
                stockBadge.setVisibility(View.VISIBLE);
                addToCartBtn.setEnabled(false);
            } else {
                stockBadge.setVisibility(View.GONE);
                addToCartBtn.setEnabled(true);
            }

            // Simplified Image Loading using Central System
            RealDeviceImageSystem.loadProductImage(context, productImage, product.getImage(), product.getProductName());
            
            // Set favorite button state
            favoriteBtn.setChecked(true); // Since this matches FavoritesActivity usage
        }
    }
}
