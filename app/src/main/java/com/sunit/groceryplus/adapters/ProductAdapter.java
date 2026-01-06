package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.ProductDetailActivity;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.FavoriteRepository;
import com.sunit.groceryplus.utils.ProductImageLoader;
import com.sunit.groceryplus.utils.AnimationUtils;
import com.sunit.groceryplus.utils.UIComponents;

import java.util.List;

/** Adapter for displaying the main product catalog grid with dynamic data binding, cart/wishlist actions, stock validation, and modern UI animations. */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    // Infrastructure
    private static final String TAG = "ProductAdapter";
    private Context context;
    private List<Product> productList;
    private DatabaseHelper dbHelper;
    private FavoriteRepository favoriteRepository;
    private int userId;
    private OnCartUpdateListener cartUpdateListener;

    /** Interface for notifying parent of cart content changes. */
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

    /** Sets the listener for cart update events. */
    public void setCartUpdateListener(OnCartUpdateListener listener) {
        this.cartUpdateListener = listener;
    }

    /**
     * Updates the displayed list (used for Search filtering).
     */
    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    /** Directly updates the product list data. */
    public void updateProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use existing product layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_card_modern, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        
        if (product == null) {
            Log.e(ProductAdapter.class.getSimpleName(), "Product is null at position: " + position);
            return;
        }
        
        // Apply entrance animation
        AnimationUtils.slideInFromBottom(holder.itemView, 300 + (position * 50));
        
        holder.productName.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");
        holder.productPrice.setText(String.format("Rs. %.2f", product.getPrice()));
        
        // Apply modern UI components
        UIComponents.createStockIndicator(holder.stockBadge, product.getStockQuantity());
        UIComponents.createPriceTag(holder.productPrice);
        UIComponents.createRatingBackground(holder.productRatingBadge, (float) product.getRating());
        
        // Category Name (if available, otherwise placeholder)
        holder.productCategory.setText(product.getCategoryName() != null ? product.getCategoryName() : "Groceries");

        if (product.getVendorName() != null && !product.getVendorName().isEmpty()) {
            holder.productVendor.setText(product.getVendorName());
            holder.productVendor.setVisibility(View.VISIBLE);
        } else {
            holder.productVendor.setVisibility(View.GONE);
        }

        // Load product image with better fallbacks
        String imageName = product.getImage();
        if (imageName != null && !imageName.isEmpty() &&
                (imageName.startsWith("content://") || imageName.startsWith("file://") || imageName.startsWith("android.resource://") ||
                        imageName.startsWith("http://") || imageName.startsWith("https://"))) {
            ProductImageLoader.load(context, holder.productImage, imageName, getSpecificImageForProduct(product.getProductName()));
        } else if (imageName != null && !imageName.isEmpty()) {
            int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                holder.productImage.setImageResource(resourceId);
            } else {
                int specificImage = getSpecificImageForProduct(product.getProductName());
                holder.productImage.setImageResource(specificImage);
            }
        } else {
            int specificImage = getSpecificImageForProduct(product.getProductName());
            holder.productImage.setImageResource(specificImage);
        }

        // Stock Badge Logic with enhanced UI
        if (product.getStockQuantity() <= 0) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            holder.addToCartBtn.setEnabled(false);
            holder.addToCartBtn.setAlpha(0.5f);
        } else if (product.getStockQuantity() < 10) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            holder.addToCartBtn.setEnabled(true);
            holder.addToCartBtn.setAlpha(1.0f);
        } else {
            holder.stockBadge.setVisibility(View.GONE);
            holder.addToCartBtn.setEnabled(true);
            holder.addToCartBtn.setAlpha(1.0f);
        }

        // Cart Quantity Logic with null safety
        int currentQty = 0;
        try {
            currentQty = dbHelper.getProductQuantityInCart(userId, product.getProductId());
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart quantity", e);
        }
        updateQuantityUI(holder, currentQty);

        // Add to Cart Button Logic
        holder.addToCartBtn.setOnClickListener(v -> {
            if (userId == -1) {
                Toast.makeText(context, "Please login to add items", Toast.LENGTH_SHORT).show();
                return;
            }
            AnimationUtils.buttonPressAnimation(v);
            
            try {
                // Validate stock before adding
                if (!dbHelper.validateStock(product.getProductId(), 1)) {
                    Toast.makeText(context, "Product is out of stock", Toast.LENGTH_SHORT).show();
                    AnimationUtils.shakeAnimation(v);
                    return;
                }
                
                long result = dbHelper.addToCart(userId, product.getProductId(), 1);
                if (result != -1) {
                    updateQuantityUI(holder, 1);
                    if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
                    AnimationUtils.successAnimation(holder.quantityLayout);
                } else {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                    AnimationUtils.shakeAnimation(v);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding to cart", e);
                Toast.makeText(context, "Error adding to cart", Toast.LENGTH_SHORT).show();
                AnimationUtils.shakeAnimation(v);
            }
        });

        // Increase Quantity Button
        holder.btnPlus.setOnClickListener(v -> {
            AnimationUtils.buttonPressAnimation(v);
            try {
                int newQty = dbHelper.getProductQuantityInCart(userId, product.getProductId()) + 1;
                
                // Validate stock
                if (!dbHelper.validateStock(product.getProductId(), newQty)) {
                    Toast.makeText(context, "Cannot add more than available stock", Toast.LENGTH_SHORT).show();
                    AnimationUtils.shakeAnimation(v);
                    return;
                }
                
                dbHelper.updateCartQuantity(userId, product.getProductId(), newQty);
                updateQuantityUI(holder, newQty);
                if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
            } catch (Exception e) {
                Toast.makeText(context, "Error updating quantity", Toast.LENGTH_SHORT).show();
                AnimationUtils.shakeAnimation(v);
            }
        });

        // Decrease Quantity Button
        holder.btnMinus.setOnClickListener(v -> {
            AnimationUtils.buttonPressAnimation(v);
            try {
                int current = dbHelper.getProductQuantityInCart(userId, product.getProductId());
                if (current > 1) {
                    dbHelper.updateCartQuantity(userId, product.getProductId(), current - 1);
                    updateQuantityUI(holder, current - 1);
                } else {
                    dbHelper.removeFromCart(userId, product.getProductId());
                    updateQuantityUI(holder, 0);
                }
                if (cartUpdateListener != null) cartUpdateListener.onCartUpdated();
            } catch (Exception e) {
                Toast.makeText(context, "Error updating quantity", Toast.LENGTH_SHORT).show();
                AnimationUtils.shakeAnimation(v);
            }
        });

        // Navigate to Product Detail
        holder.itemView.setOnClickListener(v -> {
            AnimationUtils.scaleUp(v, 200);
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProductId());
            intent.putExtra("user_id", userId);
            context.startActivity(intent);
        });
        
        // Favorite Logic with animation and null safety
        boolean isFav = false;
        try {
            isFav = favoriteRepository.isInFavorites(userId, product.getProductId());
        } catch (Exception e) {
            Log.e(TAG, "Error checking favorite status", e);
        }
        holder.favoriteBtn.setChecked(isFav);
        
        // Rating Badge
        if (product.getRating() > 0) {
            holder.productRatingBadge.setVisibility(View.VISIBLE);
            holder.productRatingBadge.setText(String.format("%.1f ★", product.getRating()));
        } else {
            holder.productRatingBadge.setVisibility(View.GONE);
        }

        holder.favoriteBtn.setOnClickListener(v -> {
            AnimationUtils.pulse(v, 300);
            try {
                if (holder.favoriteBtn.isChecked()) {
                    favoriteRepository.addToFavorites(userId, product.getProductId());
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteRepository.removeFromFavorites(userId, product.getProductId());
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error updating favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Updates the quantity toggle UI visibility and values. */
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

    /** Resolves specific local drawable resources based on product name keywords. */
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

    /** ViewHolder for Product Item. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
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