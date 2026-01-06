package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.CategoryRepository;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.ProductRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminProductAdapter;
import com.sunit.groceryplus.adapters.DrawableImageAdapter;
import com.sunit.groceryplus.models.Category;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.Vendor;
import com.sunit.groceryplus.utils.ImageStorageManager;
import com.sunit.groceryplus.utils.ProductImageLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/** ProductManagementActivity - Core admin interface for viewing, adding, editing, and deleting products with image selection and category/vendor assignment. */
public class ProductManagementActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView productsRv;
    private FloatingActionButton addProductFab;

    // Data Repositories & Helpers
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private AdminProductAdapter adapter;
    private List<Category> categories;
    private List<Vendor> vendors;
    private DatabaseHelper dbHelper;

    // Image Picker Components
    private ActivityResultLauncher<String[]> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri pendingCameraUri;
    private ImageView activeImagePreview;
    private String selectedImageValue;

    /**
     * Initializes activity, setups UI/Repo/Recycler, loads data and sets up image pickers.
     * @param savedInstanceState Saved instance state
     */
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

        initImagePickers();
    }

    /**
     * Registers Activity Result Launchers for picking images from gallery and taking photos.
     */
    private void initImagePickers() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri == null) return;
            try {
                final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (Exception ignored) {
            }

            // Save image permanently
            String permanentPath = ImageStorageManager.saveImagePermanently(
                this, uri, ImageStorageManager.ImageType.PRODUCT, "product_image");
            
            if (permanentPath != null) {
                selectedImageValue = permanentPath;
                if (activeImagePreview != null) {
                    ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                }
                Toast.makeText(this, "Image saved permanently", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to temporary URI if permanent storage fails
                selectedImageValue = uri.toString();
                if (activeImagePreview != null) {
                    ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                }
                Toast.makeText(this, "Using temporary image storage", Toast.LENGTH_LONG).show();
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success != null && success && pendingCameraUri != null) {
                // Save camera image permanently
                String permanentPath = ImageStorageManager.saveImagePermanently(
                    this, pendingCameraUri, ImageStorageManager.ImageType.PRODUCT, "product_camera");
                
                if (permanentPath != null) {
                    selectedImageValue = permanentPath;
                    if (activeImagePreview != null) {
                        ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                    }
                    Toast.makeText(this, "Camera image saved permanently", Toast.LENGTH_SHORT).show();
                } else {
                    // Fallback to temporary URI if permanent storage fails
                    selectedImageValue = pendingCameraUri.toString();
                    if (activeImagePreview != null) {
                        ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                    }
                    Toast.makeText(this, "Using temporary camera image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Finds and assigns UI views.
     */
    private void initViews() {
        productsRv = findViewById(R.id.productsRv);
        addProductFab = findViewById(R.id.addProductFab);
    }

    /**
     * Instantiates necessary repositories for data access.
     */
    private void initRepositories() {
        productRepository = new ProductRepository(this);
        categoryRepository = new CategoryRepository(this);
    }

    /**
     * Loads categories for spinner population.
     */
    private void loadCategories() {
        categories = categoryRepository.getAllCategories();
    }

    /**
     * Loads vendors for spinner population.
     */
    private void loadVendors() {
        vendors = dbHelper.getAllVendors();
    }

    /**
     * Sets up RecyclerView layout manager and adapter.
     */
    private void setupRecyclerView() {
        productsRv.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new AdminProductAdapter(this, new ArrayList<>(), productRepository, categories, this);
        productsRv.setAdapter(adapter);
    }

    /**
     * Fetches all products and updates the adapter.
     * Forces sample data refresh if list is empty.
     */
    private void loadProducts() {
        List<Product> products = productRepository.getAllProducts();
        adapter.updateProducts(products);
        
        // Debug: Log product count
        android.util.Log.d("ProductManagement", "Loaded " + products.size() + " products");
        
        // If no products, force refresh sample data
        if (products.isEmpty()) {
            android.util.Log.d("ProductManagement", "No products found, forcing sample data refresh...");
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.forceRefreshSampleData();
            
            // Reload products after refresh
            products = productRepository.getAllProducts();
            adapter.updateProducts(products);
            android.util.Log.d("ProductManagement", "After refresh: " + products.size() + " products");
        }
    }

    /**
     * Sets click listeners for FAB and test debug actions.
     */
    private void setClickListeners() {
        addProductFab.setOnClickListener(v -> showProductDialog(null));
        
        // Add test notification button for debugging
        findViewById(R.id.toolbar).setOnLongClickListener(v -> {
            // Test notification when toolbar is long-pressed
            com.sunit.groceryplus.utils.GroceryNotificationManager.testNotification(this, 1);
            Toast.makeText(this, "Test notification sent!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    /**
     * Displays a comprehensive dialog for adding or editing a product.
     * Handles inputs for Name, Price, Description, Stock, Category, Vendor, and Image.
     * @param product Product to edit, or null to create new.
     */
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
        ImageView imagePreviewIv = dialogView.findViewById(R.id.productImagePreviewIv);
        Button pickImageGalleryBtn = dialogView.findViewById(R.id.pickImageGalleryBtn);
        Button pickImageCameraBtn = dialogView.findViewById(R.id.pickImageCameraBtn);
        Button pickImageStockBtn = dialogView.findViewById(R.id.pickImageStockBtn);
        Spinner imageSpinner = dialogView.findViewById(R.id.productImageSpinner);
        TextInputEditText stockEt = dialogView.findViewById(R.id.productStockEt);
        Spinner vendorSpinner = dialogView.findViewById(R.id.vendorSpinner);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        activeImagePreview = imagePreviewIv;
        selectedImageValue = null;

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

        imageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) imageSpinner.getSelectedItem();
                // Only change image if admin hasn't already selected an image and this is a new product
                if (selected != null && selectedImageValue == null && product == null) {
                    selectedImageValue = selected;
                    if (imagePreviewIv != null) {
                        ProductImageLoader.load(ProductManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                    }
                }
                // For editing, only change if admin explicitly selects a different drawable
                else if (selected != null && product != null && 
                         selectedImageValue != null && !selectedImageValue.startsWith("content://") && 
                         !selectedImageValue.startsWith("file://") && !ImageStorageManager.isPermanentStoragePath(selectedImageValue) &&
                         !selected.equals(selectedImageValue)) {
                    selectedImageValue = selected;
                    if (imagePreviewIv != null) {
                        ProductImageLoader.load(ProductManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                    }
                }
                // Save drawable image permanently when selected for new products
                else if (selected != null && selectedImageValue != null && 
                         !selectedImageValue.startsWith("content://") && !selectedImageValue.startsWith("file://") && 
                         !ImageStorageManager.isPermanentStoragePath(selectedImageValue) && !selected.equals(selectedImageValue)) {
                    // Convert drawable to permanent file
                    String permanentPath = saveDrawableAsPermanentImage(selected);
                    if (permanentPath != null) {
                        selectedImageValue = permanentPath;
                        if (imagePreviewIv != null) {
                            ProductImageLoader.load(ProductManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                        }
                        Toast.makeText(ProductManagementActivity.this, "Stock image saved permanently", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Populate if editing
        if (product != null) {
            nameEt.setText(product.getProductName());
            priceEt.setText(String.valueOf(product.getPrice()));
            descriptionEt.setText(product.getDescription());
            stockEt.setText(String.valueOf(product.getStockQuantity()));

            selectedImageValue = product.getImage();
            if (imagePreviewIv != null) {
                ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
            }

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

            // Set image spinner selection only if it's a drawable resource
            String img = product.getImage();
            if (img != null && !img.startsWith("content://") && !img.startsWith("file://") && 
                !img.startsWith("android.resource://") && !img.startsWith("http://") && !img.startsWith("https://") &&
                !ImageStorageManager.isPermanentStoragePath(img)) {
                for (int i = 0; i < drawableNames.size(); i++) {
                    if (drawableNames.get(i).equals(img)) {
                        imageSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if (product == null) {
            // Default to first item only for new products
            selectedImageValue = (String) imageSpinner.getSelectedItem();
            if (imagePreviewIv != null) {
                ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
            }
        }

        if (pickImageGalleryBtn != null) {
            pickImageGalleryBtn.setOnClickListener(v -> {
                if (pickImageLauncher != null) {
                    pickImageLauncher.launch(new String[]{"image/*"});
                }
            });
        }

        if (pickImageCameraBtn != null) {
            pickImageCameraBtn.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 501);
                    Toast.makeText(this, "Please allow camera permission", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    File imagesDir = new File(getCacheDir(), "images");
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs();
                    }

                    File imageFile = File.createTempFile("product_", ".jpg", imagesDir);
                    pendingCameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                    if (takePictureLauncher != null) {
                        takePictureLauncher.launch(pendingCameraUri);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (pickImageStockBtn != null) {
            pickImageStockBtn.setOnClickListener(v -> showDrawableSelectorDialog());
        }

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String priceStr = priceEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();
            String image = selectedImageValue != null ? selectedImageValue : (String) imageSpinner.getSelectedItem();
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
                // Update - cleanup old image if it was changed and is a permanent storage path
                String oldImage = product.getImage();
                if (image != null && oldImage != null && !image.equals(oldImage)) {
                    if (ImageStorageManager.isPermanentStoragePath(oldImage)) {
                        ImageStorageManager.deleteImage(oldImage);
                    }
                }
                
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 501) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted. Tap Camera again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Converts a drawable resource to a permanent image file.
     * @param drawableName Name of the drawable resource
     * @return Permanent file path, or null if failed
     */
    private String saveDrawableAsPermanentImage(String drawableName) {
        try {
            // Get drawable resource ID
            int resId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            if (resId == 0) {
                return null;
            }
            
            // Load drawable as bitmap
            android.graphics.drawable.Drawable drawable = getResources().getDrawable(resId);
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), 
                android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            
            // Create permanent file
            File storageDir = ImageStorageManager.getStorageDirectory(this, ImageStorageManager.ImageType.PRODUCT);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                return null;
            }
            
            String uniqueFileName = drawableName + "_" + 
                new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date()) + 
                ".png";
            File destinationFile = new File(storageDir, uniqueFileName);
            
            // Save bitmap to file
            java.io.FileOutputStream out = new java.io.FileOutputStream(destinationFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            
            // Recycle bitmap
            bitmap.recycle();
            
            return destinationFile.getAbsolutePath();
            
        } catch (Exception e) {
            android.util.Log.e("ProductManagement", "Error saving drawable as permanent image", e);
            return null;
        }
    }

    /**
     * Shows a confirmation dialog for removing a product.
     * @param product The product to remove.
     */
    public void showDeleteConfirmationDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getProductName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete associated image if it's a permanent storage path
                    String imagePath = product.getImage();
                    if (imagePath != null && ImageStorageManager.isPermanentStoragePath(imagePath)) {
                        ImageStorageManager.deleteImage(imagePath);
                    }
                    
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

    /**
     * Shows a dialog listing reviews for the selected product.
     * @param product The product to view reviews for.
     */
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

    /**
     * Shows a dialog to select an image from app resources.
     */
    private void showDrawableSelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_drawable_selector, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Get drawable images
        List<DrawableImageAdapter.DrawableImage> drawableImages = getDrawableImages();
        
        // Setup adapter
        DrawableImageAdapter adapter = new DrawableImageAdapter(this, drawableImages);
        android.widget.GridView gridView = dialogView.findViewById(R.id.drawableGridView);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedPosition(position);
        });

        // Set up dialog buttons properly
        builder.setPositiveButton("Select", (dialogInterface, which) -> {
            DrawableImageAdapter.DrawableImage selectedImage = adapter.getSelectedImage();
            if (selectedImage != null) {
                // Convert drawable to permanent file
                String permanentPath = saveDrawableAsPermanentImage(selectedImage.getResourceName());
                if (permanentPath != null) {
                    selectedImageValue = permanentPath;
                    if (activeImagePreview != null) {
                        ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                    }
                    Toast.makeText(this, "Stock image saved permanently", Toast.LENGTH_SHORT).show();
                } else {
                    // Fallback to drawable name if conversion fails
                    selectedImageValue = selectedImage.getResourceName();
                    if (activeImagePreview != null) {
                        ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
            // Do nothing, just dismiss
        });

        builder.show();
    }

    /**
     * Retrieves list of all drawable resources for the image selector dialog.
     */
    private List<DrawableImageAdapter.DrawableImage> getDrawableImages() {
        List<DrawableImageAdapter.DrawableImage> images = new ArrayList<>();
        
        try {
            Field[] drawables = R.drawable.class.getFields();
            for (Field field : drawables) {
                String name = field.getName();
                
                // Only skip the most obvious system/internal drawables
                // Keep everything else - PNG, JPEG, XML vectors, backgrounds, etc.
                if (name.startsWith("abc_") || 
                    name.startsWith("design_") || name.startsWith("mtrl_") ||
                    name.startsWith("googleg_") || name.startsWith("common_") ||
                    name.startsWith("avd_")) {
                    continue;
                }
                
                // Include ALL remaining drawables - PNG, JPEG, XML, everything!
                images.add(new DrawableImageAdapter.DrawableImage(name, name));
            }
        } catch (Exception e) {
            android.util.Log.e("ProductManagement", "Error loading drawable images", e);
        }
        
        return images;
    }

    /**
     * Formats drawable name for display (e.g., "my_image" -> "My Image").
     */
    private String formatName(String drawableName) {
        // Convert drawable_name to Display Name
        String formatted = drawableName.replaceAll("_", " ")
                .replaceAll("([A-Z])", " $1")
                .trim();
        
        // Capitalize first letter of each word
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(Character.toUpperCase(word.charAt(0)))
                       .append(word.substring(1).toLowerCase());
            }
        }
        
        return result.toString();
    }

    /**
     * Handles options menu items (Home button).
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
