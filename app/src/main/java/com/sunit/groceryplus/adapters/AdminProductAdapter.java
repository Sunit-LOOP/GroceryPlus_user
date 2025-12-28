package com.sunit.groceryplus.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.admin.ProductManagementActivity;
import com.sunit.groceryplus.models.Category;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.ProductRepository;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private ProductRepository productRepository;
    private List<Category> categoryList;
    private ProductManagementActivity activity;

    public AdminProductAdapter(Context context, List<Product> productList, ProductRepository productRepository, List<Category> categoryList, ProductManagementActivity activity) {
        this.context = context;
        this.productList = productList;
        this.productRepository = productRepository;
        this.categoryList = categoryList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTv.setText(product.getProductName());
        holder.productPriceTv.setText("रु " + String.format("%.2f", product.getPrice()));
        if (holder.productDescriptionTv != null) {
            holder.productDescriptionTv.setText(product.getDescription());
        }
        
        holder.productStockTv.setText("Stock: " + product.getStockQuantity());
        if (product.getStockQuantity() <= 0) {
            holder.productStockTv.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.productStockTv.setText("Out of Stock");
        } else if (product.getStockQuantity() < 10) {
            holder.productStockTv.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.productStockTv.setText("Low Stock: " + product.getStockQuantity());
        } else {
            holder.productStockTv.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        if (product.getVendorName() != null) {
            holder.productVendorTv.setText("Vendor: " + product.getVendorName());
        } else {
            holder.productVendorTv.setText("Vendor: Not Assigned");
        }

        // Set product image based on image name with better fallbacks
        String imageName = product.getImage();
        if (imageName != null && !imageName.isEmpty()) {
            int resourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                holder.productImageIv.setImageResource(resourceId);
            } else {
                // Try to assign specific images based on product name
                int specificImage = getSpecificImageForProduct(product.getProductName());
                holder.productImageIv.setImageResource(specificImage);
            }
        } else {
            // Assign specific images based on product name
            int specificImage = getSpecificImageForProduct(product.getProductName());
            holder.productImageIv.setImageResource(specificImage);
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

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTv, productPriceTv, productDescriptionTv, productStockTv, productVendorTv;
        ImageView productImageIv;
        Button editBtn, deleteBtn;
        ImageButton viewReviewsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Corrected the view IDs to match the actual layout
            productNameTv = itemView.findViewById(R.id.productNameTv);
            productPriceTv = itemView.findViewById(R.id.productPriceTv);
            productDescriptionTv = itemView.findViewById(R.id.productDescriptionTv);
            productStockTv = itemView.findViewById(R.id.productStockTv);
            productVendorTv = itemView.findViewById(R.id.productVendorTv);
            productImageIv = itemView.findViewById(R.id.productImageIv);
            viewReviewsBtn = itemView.findViewById(R.id.viewReviewsBtn);
            editBtn = itemView.findViewById(R.id.editProductBtn);
            deleteBtn = itemView.findViewById(R.id.deleteProductBtn);
            
            viewReviewsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        activity.showReviewsDialog(productList.get(position));
                    }
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        activity.showProductDialog(productList.get(position));
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        activity.showDeleteConfirmationDialog(productList.get(position));
                    }
                }
            });
        }
    }
}