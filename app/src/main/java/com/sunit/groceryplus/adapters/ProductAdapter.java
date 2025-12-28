package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sunit.groceryplus.ProductDetailActivity;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.FavoriteRepository;

import java.util.List;
import java.io.File;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private FavoriteRepository favoriteRepository;
    private int userId;
    private OnCartUpdateListener cartUpdateListener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public ProductAdapter(Context context, List<Product> productList, int userId) {
        this.context = context;
        this.productList = productList;
        this.dbHelper = new DatabaseHelper(context);
        this.favoriteRepository = new FavoriteRepository(context);
        this.userId = userId;
    }

    public void setCartUpdateListener(OnCartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }

    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    public void updateProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Updated to use modern modern card layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_card_modern, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(String.format("रु %.2f", product.getPrice()));
        
        // Category Name (if available, otherwise placeholder)
        // Ideally pass category name or fetch it, for now hardcoded or skipped if not in model
         holder.productCategory.setText("Groceries"); // Placeholder or fetch logic

        if (product.getVendorName() != null) {
            holder.productVendor.setText(product.getVendorName());
            holder.productVendor.setVisibility(View.VISIBLE);
        } else {
            holder.productVendor.setVisibility(View.GONE);
        }

        // Load product image with better fallbacks
        String imageName = product.getImage();
        if (imageName != null && !imageName.isEmpty()) {
            // Check if it's a drawable resource name
            int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                holder.productImage.setImageResource(resourceId);
            } else {
                // Try to assign specific images based on product name
                int specificImage = getSpecificImageForProduct(product.getProductName());
                holder.productImage.setImageResource(specificImage);
            }
        } else {
            // Assign specific images based on product name
            int specificImage = getSpecificImageForProduct(product.getProductName());
            holder.productImage.setImageResource(specificImage);
        }

        // Stock Badge Logic
        if (product.getStockQuantity() <= 0) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            holder.stockBadge.setText("Out of Stock");
            holder.stockBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F44336")));
            holder.addToCartBtn.setEnabled(false);
            holder.addToCartBtn.setAlpha(0.5f);
        } else if (product.getStockQuantity() < 10) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            holder.stockBadge.setText("Only " + product.getStockQuantity() + " left");
            holder.stockBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800")));
            holder.addToCartBtn.setEnabled(true);
            holder.addToCartBtn.setAlpha(1.0f);
        } else {
            holder.stockBadge.setVisibility(View.GONE);
            holder.addToCartBtn.setEnabled(true);
            holder.addToCartBtn.setAlpha(1.0f);
        }

        // Cart Quantity Logic
        int currentQty = dbHelper.getProductQuantityInCart(userId, product.getProductId());
        updateQuantityUI(holder, currentQty);

        holder.addToCartBtn.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(context, "Please login to add items", Toast.LENGTH_SHORT).show();
                return;
            }
            long result = dbHelper.addToCart(userId, product.getProductId(), 1);
            if (result != -1) {
                updateQuantityUI(holder, 1);
                if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
                v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            } else {
                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int newQty = dbHelper.getProductQuantityInCart(userId, product.getProductId()) + 1;
            if (newQty > product.getStockQuantity()) {
                Toast.makeText(context, "Cannot add more than available stock", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.updateCartQuantity(userId, product.getProductId(), newQty);
            updateQuantityUI(holder, newQty);
            if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int current = dbHelper.getProductQuantityInCart(userId, product.getProductId());
            if (current > 1) {
                dbHelper.updateCartQuantity(userId, product.getProductId(), current - 1);
                updateQuantityUI(holder, current - 1);
            } else {
                dbHelper.removeFromCart(userId, product.getProductId());
                updateQuantityUI(holder, 0);
            }
            if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("user_id", userId);
            context.startActivity(intent);
        });
        
        // Favorite Logic
        boolean isFav = favoriteRepository.isInFavorites(userId, product.getProductId());
        holder.favoriteBtn.setChecked(isFav);
        
        // Rating Badge
        if (product.getRating() > 0) {
            holder.productRatingBadge.setVisibility(View.VISIBLE);
            holder.productRatingBadge.setText(String.format("%.1f ★", product.getRating()));
        } else {
            holder.productRatingBadge.setVisibility(View.GONE);
        }

        holder.favoriteBtn.setOnClickListener(v -> {
            if (holder.favoriteBtn.isChecked()) {
                favoriteRepository.addToFavorites(userId, product.getProductId());
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                favoriteRepository.removeFromFavorites(userId, product.getProductId());
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Animation
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
    }

    private void updateQuantityUI(ViewHolder holder, int quantity) {
        if (quantity > 0) {
            holder.addToCartBtn.setVisibility(View.GONE);
            holder.quantityLayout.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.valueOf(quantity));
        } else {
            holder.addToCartBtn.setVisibility(View.VISIBLE);
            holder.quantityLayout.setVisibility(View.GONE);
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
        
        // Detailed mapping based on available drawables
        if (lowerName.contains("milk")) {
            if (lowerName.contains("skim")) return R.drawable.skim_milk;
            return R.drawable.bottle_milk;
        } else if (lowerName.contains("cheese")) {
            return R.drawable.cheese_slice;
        } else if (lowerName.contains("curd") || lowerName.contains("yogurt") || lowerName.contains("dahi")) {
            return R.drawable.dahi;
        } else if (lowerName.contains("tomato")) {
            return R.drawable.tomato_red;
        } else if (lowerName.contains("cabbage")) {
            return R.drawable.cabbage;
        } else if (lowerName.contains("cauliflower")) {
            return R.drawable.cauliflower;
        } else if (lowerName.contains("lettuce")) {
            return R.drawable.lettuce_leaf;
        } else if (lowerName.contains("paneer")) {
            return R.drawable.paneer_cubes;
        } else if (lowerName.contains("bottle") && lowerName.contains("gourd")) {
            return R.drawable.bottle_gourd;
        } else if (lowerName.contains("okra") || lowerName.contains("lady") || lowerName.contains("vindi")) {
            return R.drawable.vindi;
        } else if (lowerName.contains("green") && (lowerName.contains("vegetable") || lowerName.contains("leaf"))) {
            if (lowerName.contains("small")) return R.drawable.small_green_leaf_vegetable;
            return R.drawable.green_vegetable;
        } 
        // New Mappings
        else if (lowerName.contains("apple")) {
            return R.drawable.apple;
        } else if (lowerName.contains("banana")) {
            return R.drawable.banana;
        } else if (lowerName.contains("bread") || lowerName.contains("bakery")) {
            return R.drawable.bread;
        } else if (lowerName.contains("rice") || lowerName.contains("staple")) {
            return R.drawable.rice_sack;
        } else if (lowerName.contains("oil")) {
            return R.drawable.oil_bottle;
        } else if (lowerName.contains("juice") || lowerName.contains("drink") || lowerName.contains("beverage")) {
            return R.drawable.juice_bottle;
        }
        else {
            return R.drawable.product_icon;
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productCategory, stockBadge, tvQuantity, productRatingBadge, productVendor;
        View addToCartBtn, quantityLayout;
        ImageButton btnMinus, btnPlus;
        ToggleButton favoriteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
            productVendor = itemView.findViewById(R.id.productVendor);
            productRatingBadge = itemView.findViewById(R.id.productRatingBadge);
            stockBadge = itemView.findViewById(R.id.stockBadge);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            quantityLayout = itemView.findViewById(R.id.quantityLayout);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        }
    }
}