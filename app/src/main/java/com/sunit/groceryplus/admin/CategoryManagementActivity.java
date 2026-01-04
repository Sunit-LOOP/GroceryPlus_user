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
import com.sunit.groceryplus.utils.ProductImageLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * CategoryManagementActivity - Admin interface for managing product categories.
 * 
 * This activity allows the admin to view, add, edit, and delete product categories.
 * It uses a dialog-based interface for input and interacts with the CategoryRepository.
 * 
 * Key Features:
 * - List all categories
 * - Add new category
 * - Edit existing category
 * - Delete category (with confirmation)
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView categoriesRv;
    private FloatingActionButton addCategoryFab;
    private AdminCategoryAdapter adapter;
    private CategoryRepository categoryRepository;

    private ActivityResultLauncher<String[]> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri pendingCameraUri;

    private ImageView activeImagePreview;
    private String selectedImageValue;

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

    private void initImagePickers() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri == null) return;
            try {
                final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, flags);
            } catch (Exception ignored) {
            }

            selectedImageValue = uri.toString();
            if (activeImagePreview != null) {
                ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success != null && success && pendingCameraUri != null) {
                selectedImageValue = pendingCameraUri.toString();
                if (activeImagePreview != null) {
                    ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                }
            }
        });
    }

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

    private void loadCategories() {
        List<Category> categories = categoryRepository.getAllCategories();
        adapter.updateCategories(categories);
    }

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
                if (selected != null && (selectedImageValue == null || !selectedImageValue.startsWith("content://") && !selectedImageValue.startsWith("file://"))) {
                    selectedImageValue = selected;
                    ProductImageLoader.load(CategoryManagementActivity.this, imagePreviewIv, selectedImageValue, R.drawable.product_icon);
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

            // Set spinner selection if it's a drawable
            if (selectedImageValue != null) {
                for (int i = 0; i < drawableNames.size(); i++) {
                    if (drawableNames.get(i).equals(selectedImageValue)) {
                        imageSpinner.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            // Default to first item for new categories
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
                // Update
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
                        selectedImageValue = selectedImage.getResourceName();
                        if (activeImagePreview != null) {
                            ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.product_icon);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedPosition(position);
        });

        builder.show();
    }

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

    private void showDeleteConfirmationDialog(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete " + category.getCategoryName() + "? Products in this category will remain but may lose category association.")
                .setPositiveButton("Delete", (dialog, which) -> {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
