package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.CategoryRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminCategoryAdapter;
import com.sunit.groceryplus.adapters.DrawableImageAdapter;
import com.sunit.groceryplus.models.Category;
import com.sunit.groceryplus.utils.ImageStorageManager;
import com.sunit.groceryplus.utils.ProductImageLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/** CategoryManagementActivity - Admin interface for viewing, adding, editing, and deleting product categories using a dialog-based UI. */
public class CategoryManagementActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView categoriesRv;
    private FloatingActionButton addCategoryFab;
    
    // Adapter & Data
    private AdminCategoryAdapter adapter;
    private CategoryRepository categoryRepository;

    // Image Selection
    private ActivityResultLauncher<String[]> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri pendingCameraUri;
    private ImageView activeImagePreview;
    private String selectedImageValue;

    /**
     * Initializes activity, setups UI including Recycler View and FAB, and initializes image pickers.
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        categoriesRv = findViewById(R.id.categoriesRv);
        addCategoryFab = findViewById(R.id.addCategoryFab);

        categoryRepository = new CategoryRepository(this);

        setupRecyclerView();
        loadCategories();

        addCategoryFab.setOnClickListener(v -> showCategoryDialog(null));

        initImagePickers();
    }

    /**
     * Registers Activity Result Launchers for picking images from gallery and taking photos with camera.
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
                this, uri, ImageStorageManager.ImageType.CATEGORY, "category_image");
            
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
                    this, pendingCameraUri, ImageStorageManager.ImageType.CATEGORY, "category_camera");
                
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
     * Configures the RecyclerView with AdminCategoryAdapter and handles edit/delete callbacks.
     */
    private void setupRecyclerView() {
        categoriesRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCategoryAdapter(this, new ArrayList<>(), new AdminCategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEditClick(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                showDeleteConfirmationDialog(category);
            }
        });
        categoriesRv.setAdapter(adapter);
    }

    /**
     * Fetches all categories from repository and updates the adapter.
     */
    private void loadCategories() {
        List<Category> categories = categoryRepository.getAllCategories();
        adapter.updateCategories(categories);
    }

    /**
     * Shows a dialog to add a new category or edit an existing one.
     * @param category The category to edit, or null to create a new one.
     */
    private void showCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // UI References
        TextInputEditText nameEt = dialogView.findViewById(R.id.categoryNameEt);
        TextInputEditText descriptionEt = dialogView.findViewById(R.id.categoryDescriptionEt);
        
        ImageView imagePreviewIv = dialogView.findViewById(R.id.categoryImagePreviewIv);
        Button pickImageGalleryBtn = dialogView.findViewById(R.id.pickImageGalleryBtn);
        Button pickImageCameraBtn = dialogView.findViewById(R.id.pickImageCameraBtn);
        Button pickImageStockBtn = dialogView.findViewById(R.id.pickImageStockBtn);
        Spinner imageSpinner = dialogView.findViewById(R.id.categoryImageSpinner);
        
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        activeImagePreview = imagePreviewIv;
        selectedImageValue = null;

        // Setup Image Spinner
        List<String> drawableNames = new ArrayList<>();
        Field[] drawables = R.drawable.class.getFields();
        for (Field f : drawables) {
            String name = f.getName();
            // Filter out system/internal drawables but keep user-facing ones
            if (name.startsWith("abc_") || 
                name.startsWith("design_") || name.startsWith("mtrl_") ||
                name.startsWith("googleg_") || name.startsWith("common_") ||
                name.startsWith("avd_")) {
                continue;
            }
            drawableNames.add(name);
        }
        android.widget.ArrayAdapter<String> imageSpinnerAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drawableNames);
        imageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSpinner.setAdapter(imageSpinnerAdapter);
        imageSpinner.setVisibility(View.VISIBLE);

        imageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) imageSpinner.getSelectedItem();
                // Only change image if admin hasn't already selected an image and this is a new category
                if (selected != null && selectedImageValue == null && category == null) {
                    selectedImageValue = selected;
                    ProductImageLoader.load(CategoryManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                }
                // For editing, only change if admin explicitly selects a different drawable
                else if (selected != null && category != null && 
                         selectedImageValue != null && !selectedImageValue.startsWith("content://") && 
                         !selectedImageValue.startsWith("file://") && !ImageStorageManager.isPermanentStoragePath(selectedImageValue) &&
                         !selected.equals(selectedImageValue)) {
                    selectedImageValue = selected;
                    ProductImageLoader.load(CategoryManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                }
                // Save drawable image permanently when selected for new categories
                else if (selected != null && selectedImageValue != null && 
                         !selectedImageValue.startsWith("content://") && !selectedImageValue.startsWith("file://") && 
                         !ImageStorageManager.isPermanentStoragePath(selectedImageValue) && !selected.equals(selectedImageValue)) {
                    // Convert drawable to permanent file
                    String permanentPath = saveDrawableAsPermanentImage(selected);
                    if (permanentPath != null) {
                        selectedImageValue = permanentPath;
                        ProductImageLoader.load(CategoryManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
                        Toast.makeText(CategoryManagementActivity.this, "Stock image saved permanently", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Populate if editing
        if (category != null) {
            nameEt.setText(category.getCategoryName());
            descriptionEt.setText(category.getCategoryDescription());
            selectedImageValue = category.getImageUrl();
            ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);

            // Set spinner selection only if it's a drawable resource
            if (selectedImageValue != null && !selectedImageValue.startsWith("content://") && 
                !selectedImageValue.startsWith("file://") && !ImageStorageManager.isPermanentStoragePath(selectedImageValue)) {
                for (int i = 0; i < drawableNames.size(); i++) {
                    if (drawableNames.get(i).equals(selectedImageValue)) {
                        imageSpinner.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            // Default to first item only for new categories
            if (!drawableNames.isEmpty()) {
                selectedImageValue = drawableNames.get(0);
                ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
            }
        }

        pickImageGalleryBtn.setOnClickListener(v -> {
            if (pickImageLauncher != null) {
                pickImageLauncher.launch(new String[]{"image/*"});
            }
        });

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

                File imageFile = File.createTempFile("category_", ".jpg", imagesDir);
                pendingCameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                if (takePictureLauncher != null) {
                    takePictureLauncher.launch(pendingCameraUri);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
            }
        });

        pickImageStockBtn.setOnClickListener(v -> showDrawableSelectorDialog());

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();
            String image = selectedImageValue != null ? selectedImageValue : (String) imageSpinner.getSelectedItem();

            if (name.isEmpty()) {
                Toast.makeText(this, "Category name required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (category == null) {
                // Add
                success = categoryRepository.addCategory(name, description, image);
                if (success) {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Update - cleanup old image if it was changed and is a permanent storage path
                String oldImage = category.getImageUrl();
                if (image != null && oldImage != null && !image.equals(oldImage)) {
                    if (ImageStorageManager.isPermanentStoragePath(oldImage)) {
                        ImageStorageManager.deleteImage(oldImage);
                    }
                }
                
                success = categoryRepository.updateCategory(category.getCategoryId(), name, description, image);
                if (success) {
                    Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
            }

            if (success) {
                loadCategories();
                dialog.dismiss();
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
     * Shows a dialog with a grid of app-bundled drawable resources for image selection.
     */
    private void showDrawableSelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_drawable_selector, null);
        builder.setView(dialogView);

        // Get drawable images
        List<DrawableImageAdapter.DrawableImage> drawableImages = getDrawableImages();
        
        // Setup adapter
        DrawableImageAdapter adapter = new DrawableImageAdapter(this, drawableImages);
        android.widget.GridView gridView = dialogView.findViewById(R.id.drawableGridView);
        gridView.setAdapter(adapter);

        // Set up the dialog using the existing builder
        builder.setTitle("Select Icon")
                .setView(dialogView)
                .setPositiveButton("Select", (dialogInterface, which) -> {
                    DrawableImageAdapter.DrawableImage selectedImage = adapter.getSelectedImage();
                    if (selectedImage != null) {
                        // Convert drawable to permanent file
                        String permanentPath = saveDrawableAsPermanentImage(selectedImage.getResourceName());
                        if (permanentPath != null) {
                            selectedImageValue = permanentPath;
                            if (activeImagePreview != null) {
                                ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                            }
                            Toast.makeText(this, "Stock icon saved permanently", Toast.LENGTH_SHORT).show();
                        } else {
                            // Fallback to drawable name if conversion fails
                            selectedImageValue = selectedImage.getResourceName();
                            if (activeImagePreview != null) {
                                ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedPosition(position);
        });

        builder.show();
    }

    /**
     * Reflectively gets all drawable fields from R.drawable, filtering out system resources.
     * @return List of DrawableImage objects
     */
    private List<DrawableImageAdapter.DrawableImage> getDrawableImages() {
        List<DrawableImageAdapter.DrawableImage> drawableImages = new ArrayList<>();
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                
                // Filter out system/internal drawables
                if (name.startsWith("abc_") || 
                    name.startsWith("design_") || name.startsWith("mtrl_") ||
                    name.startsWith("googleg_") || name.startsWith("common_") ||
                    name.startsWith("avd_")) {
                    continue;
                }
                
                // Include all other drawables (PNG, JPEG, XML, etc.)
                drawableImages.add(new DrawableImageAdapter.DrawableImage(name, name));
            } catch (Exception ignored) {
            }
        }
        return drawableImages;
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
            
            // Load drawable as bitmap with optimized settings
            android.graphics.drawable.Drawable drawable = getResources().getDrawable(resId);
            int originalWidth = drawable.getIntrinsicWidth();
            int originalHeight = drawable.getIntrinsicHeight();
            
            // Limit maximum dimensions to reasonable size for category images
            int maxWidth = 512; // Smaller for category icons
            int maxHeight = 512;
            int finalWidth, finalHeight;
            
            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                // Calculate scaled dimensions maintaining aspect ratio
                float aspectRatio = (float) originalWidth / originalHeight;
                if (originalWidth > originalHeight) {
                    finalWidth = maxWidth;
                    finalHeight = (int) (maxWidth / aspectRatio);
                } else {
                    finalHeight = maxHeight;
                    finalWidth = (int) (maxHeight * aspectRatio);
                }
            } else {
                finalWidth = originalWidth;
                finalHeight = originalHeight;
            }
            
            // Create bitmap with RGB_565 for better performance on real devices
            android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                finalWidth, finalHeight, android.graphics.Bitmap.Config.RGB_565);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            drawable.setBounds(0, 0, finalWidth, finalHeight);
            drawable.draw(canvas);
            
            // Create permanent file
            File storageDir = ImageStorageManager.getStorageDirectory(this, ImageStorageManager.ImageType.CATEGORY);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                return null;
            }
            
            String uniqueFileName = drawableName + "_" + 
                new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new java.util.Date()) + 
                ".jpg"; // Use JPG for better compression
            File destinationFile = new File(storageDir, uniqueFileName);
            
            // Save bitmap to file with JPEG compression for better size/quality balance
            java.io.FileOutputStream out = new java.io.FileOutputStream(destinationFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out); // 85% quality for good balance
            out.flush(); // Ensure data is written
            out.close();
            
            // Recycle bitmap to free memory
            bitmap.recycle();
            
            android.util.Log.d("CategoryManagement", "Drawable saved permanently: " + destinationFile.getAbsolutePath());
            return destinationFile.getAbsolutePath();
            
        } catch (Exception e) {
            android.util.Log.e("CategoryManagement", "Error saving drawable as permanent image", e);
            return null;
        }
    }

    /**
     * Displays a confirmation dialog before deleting a category.
     * @param category The category to delete
     */
    private void showDeleteConfirmationDialog(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete " + category.getCategoryName() + "? Products in this category will remain but may lose category association.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete associated image if it's a permanent storage path
                    String imagePath = category.getImageUrl();
                    if (imagePath != null && ImageStorageManager.isPermanentStoragePath(imagePath)) {
                        ImageStorageManager.deleteImage(imagePath);
                    }
                    
                    boolean success = categoryRepository.deleteCategory(category.getCategoryId());
                    if (success) {
                        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                        loadCategories();
                    } else {
                        Toast.makeText(this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles toolbar menu actions (Back button).
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
