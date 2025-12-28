package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.CategoryRepository;
import com.sunit.groceryplus.ProductRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminProductAdapter;
import com.sunit.groceryplus.models.Category;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Vendor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity {

    private RecyclerView productsRv;
    private FloatingActionButton addProductFab;

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private AdminProductAdapter adapter;
    private List<Category> categories;
    private List<Vendor> vendors;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        dbHelper = new DatabaseHelper(this);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Products");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();
        initRepositories();
        loadCategories();
        loadVendors();
        setupRecyclerView();
        setClickListeners();
        loadProducts();
    }

    private void initViews() {
        productsRv = findViewById(R.id.productsRv);
        addProductFab = findViewById(R.id.addProductFab);
    }

    private void initRepositories() {
        productRepository = new ProductRepository(this);
        categoryRepository = new CategoryRepository(this);
    }

    private void loadCategories() {
        categories = categoryRepository.getAllCategories();
    }

    private void loadVendors() {
        vendors = dbHelper.getAllVendors();
    }

    private void setupRecyclerView() {
        productsRv.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new AdminProductAdapter(this, new ArrayList<>(), productRepository, categories, this);
        productsRv.setAdapter(adapter);
    }

    private void loadProducts() {
        List<Product> products = productRepository.getAllProducts();
        adapter.updateProducts(products);
    }

    private void setClickListeners() {
        addProductFab.setOnClickListener(v -> showProductDialog(null));
    }

    public void showProductDialog(Product product) {
        if (categories == null || categories.isEmpty()) {
            Toast.makeText(this, "Please add categories first before adding products", Toast.LENGTH_LONG).show();
            return;
        }

        if (vendors == null || vendors.isEmpty()) {
            Toast.makeText(this, "Please add vendors first before adding products", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // UI References
        TextInputEditText nameEt = dialogView.findViewById(R.id.productNameEt);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        TextInputEditText priceEt = dialogView.findViewById(R.id.productPriceEt);
        TextInputEditText descriptionEt = dialogView.findViewById(R.id.productDescriptionEt);
        Spinner imageSpinner = dialogView.findViewById(R.id.productImageSpinner);
        TextInputEditText stockEt = dialogView.findViewById(R.id.productStockEt);
        Spinner vendorSpinner = dialogView.findViewById(R.id.vendorSpinner);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        // Setup Category Spinner
        List<String> categoryNames = new ArrayList<>();
        for (Category cat : categories) {
            categoryNames.add(cat.getCategoryName());
        }
        ArrayAdapter<String> categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categorySpinnerAdapter);

        // Setup Vendor Spinner
        List<String> vendorNames = new ArrayList<>();
        for (Vendor v : vendors) {
            vendorNames.add(v.getVendorName());
        }
        ArrayAdapter<String> vendorSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vendorNames);
        vendorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorSpinner.setAdapter(vendorSpinnerAdapter);

        // Setup Image Spinner
        List<String> drawableNames = new ArrayList<>();
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            if (f.getName().startsWith("ic_") || f.getName().startsWith("banner_")) {
                continue;
            }
            drawableNames.add(f.getName());
        }
        ArrayAdapter<String> imageSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drawableNames);
        imageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(imageSpinnerAdapter);

        // Populate if editing
        if (product != null) {
            nameEt.setText(product.getProductName());
            priceEt.setText(String.valueOf(product.getPrice()));
            descriptionEt.setText(product.getDescription());
            stockEt.setText(String.valueOf(product.getStockQuantity()));

            // Set category spinner selection
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategoryId() == product.getCategoryId()) {
                    categorySpinner.setSelection(i);
                    break;
                }
            }

            // Set vendor spinner selection
            for (int i = 0; i < vendors.size(); i++) {
                if (vendors.get(i).getVendorId() == product.getVendorId()) {
                    vendorSpinner.setSelection(i);
                    break;
                }
            }

            // Set image spinner selection
            for (int i = 0; i < drawableNames.size(); i++) {
                if (drawableNames.get(i).equals(product.getImage())) {
                    imageSpinner.setSelection(i);
                    break;
                }
            }
        }

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String priceStr = priceEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();
            String image = (String) imageSpinner.getSelectedItem();
            String stockStr = stockEt.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            int stock = 0;
            if (!stockStr.isEmpty()) {
                try {
                    stock = Integer.parseInt(stockStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid stock quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            int selectedCategoryIndex = categorySpinner.getSelectedItemPosition();
            int categoryId = categories.get(selectedCategoryIndex).getCategoryId();

            int selectedVendorIndex = vendorSpinner.getSelectedItemPosition();
            int vendorId = vendors.get(selectedVendorIndex).getVendorId();

            boolean success;
            if (product == null) {
                // Add
                success = productRepository.addProduct(name, categoryId, price, description, image, stock, vendorId);
            } else {
                // Update
                success = productRepository.updateProduct(product.getProductId(), name, categoryId, price, description, image, stock, vendorId);
            }

            if (success) {
                Toast.makeText(this, "Product " + (product == null ? "added" : "updated") + " successfully", Toast.LENGTH_SHORT).show();
                loadProducts();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to " + (product == null ? "add" : "update") + " product", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    public void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getProductName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = productRepository.deleteProduct(product.getProductId());
                    if (success) {
                        Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showReviewsDialog(Product product) {
        List<com.sunit.groceryplus.models.Review> reviews = dbHelper.getReviewsForProduct(product.getProductId());

        if (reviews.isEmpty()) {
            Toast.makeText(this, "No reviews for this product yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reviews for " + product.getProductName());

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_view_reviews, null);
        RecyclerView reviewsRv = dialogView.findViewById(R.id.dialogReviewsRv);
        reviewsRv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        com.sunit.groceryplus.adapters.ReviewAdapter reviewAdapter = new com.sunit.groceryplus.adapters.ReviewAdapter(this, reviews);
        reviewsRv.setAdapter(reviewAdapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
