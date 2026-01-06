package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminVendorAdapter;
import com.sunit.groceryplus.adapters.DrawableImageAdapter;
import com.sunit.groceryplus.models.Vendor;
import com.sunit.groceryplus.utils.ProductImageLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/** VendorManagementActivity - Admin interface for managing store vendors, locations, and details. */
public class VendorManagementActivity extends AppCompatActivity implements AdminVendorAdapter.OnVendorActionListener {

    // UI Components
    private RecyclerView recyclerView;
    
    // Data & Adapters
    private AdminVendorAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Vendor> vendorList;

    // Image Picker Components
    private ActivityResultLauncher<String[]> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri pendingCameraUri;
    private ImageView activeImagePreview;
    private String selectedImageValue;

    /**
     * Initializes activity, sets up toolbar, views, and loads vendor data.
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_management);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.vendorToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.vendorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadVendors();

        FloatingActionButton addFab = findViewById(R.id.addVendorFab);
        addFab.setOnClickListener(v -> showVendorDialog(null));

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

            selectedImageValue = uri.toString();
            if (activeImagePreview != null) {
                ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.vendor_icon);
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success != null && success && pendingCameraUri != null) {
                selectedImageValue = pendingCameraUri.toString();
                if (activeImagePreview != null) {
                    ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.vendor_icon);
                }
            }
        });
    }

    /**
     * Fetches all vendors from the database and refreshes the adapter.
     * Inserts sample data if list is empty.
     */
    private void loadVendors() {
        vendorList = dbHelper.getAllVendors();
        
        // Proactive restoration if list is empty (e.g., after a destructive upgrade or move)
        if (vendorList.isEmpty()) {
            dbHelper.insertSampleData();
            vendorList = dbHelper.getAllVendors();
        }

        if (adapter == null) {
            adapter = new AdminVendorAdapter(this, vendorList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(vendorList);
        }
    }

    /**
     * Displays a dialog for adding or editing a vendor details.
     * @param vendor The vendor to edit, or null to create new.
     */
    private void showVendorDialog(Vendor vendor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_vendor, null);
        builder.setView(view);

        TextView titleTv = view.findViewById(R.id.dialogTitle);
        EditText nameEt = view.findViewById(R.id.vendorNameEt);
        EditText addressEt = view.findViewById(R.id.vendorAddressEt);
        EditText latEt = view.findViewById(R.id.vendorLatEt);
        EditText lngEt = view.findViewById(R.id.vendorLngEt);
        
        ImageView imagePreviewIv = view.findViewById(R.id.vendorImagePreviewIv);
        Button pickImageGalleryBtn = view.findViewById(R.id.pickImageGalleryBtn);
        Button pickImageCameraBtn = view.findViewById(R.id.pickImageCameraBtn);
        Button pickImageStockBtn = view.findViewById(R.id.pickImageStockBtn);
        Spinner imageSpinner = view.findViewById(R.id.vendorImageSpinner);
        
        EditText ratingEt = view.findViewById(R.id.vendorRatingEt);
        MaterialButton saveBtn = view.findViewById(R.id.saveVendorBtn);

        activeImagePreview = imagePreviewIv;
        
        if (vendor != null) {
            titleTv.setText("Edit Vendor");
            nameEt.setText(vendor.getVendorName());
            addressEt.setText(vendor.getAddress());
            latEt.setText(String.valueOf(vendor.getLatitude()));
            lngEt.setText(String.valueOf(vendor.getLongitude()));
            selectedImageValue = vendor.getIcon();
            ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.vendor_icon);
            ratingEt.setText(String.valueOf(vendor.getRating()));
        } else {
            titleTv.setText("Add New Vendor");
            selectedImageValue = "vendor_icon";
            ProductImageLoader.load(this, imagePreviewIv, selectedImageValue, R.drawable.vendor_icon);
        }

        AlertDialog dialog = builder.create();

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

                File imageFile = File.createTempFile("vendor_", ".jpg", imagesDir);
                pendingCameraUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                if (takePictureLauncher != null) {
                    takePictureLauncher.launch(pendingCameraUri);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
            }
        });

        pickImageStockBtn.setOnClickListener(v -> showDrawableSelectorDialog());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();
            String latStr = latEt.getText().toString().trim();
            String lngStr = lngEt.getText().toString().trim();
            String icon = selectedImageValue != null ? selectedImageValue : "";
            String ratingStr = ratingEt.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || latStr.isEmpty() || lngStr.isEmpty() || icon.isEmpty() || ratingStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double lat = Double.parseDouble(latStr);
            double lng = Double.parseDouble(lngStr);
            double rating = Double.parseDouble(ratingStr);

            boolean success;
            if (vendor == null) {
                long id = dbHelper.addVendor(name, address, lat, lng, icon, rating);
                success = id != -1;
            } else {
                success = dbHelper.updateVendor(vendor.getVendorId(), name, address, lat, lng, icon, rating);
            }

            if (success) {
                Toast.makeText(this, "Vendor saved successfully", Toast.LENGTH_SHORT).show();
                loadVendors();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Failed to save vendor", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    /**
     * Callback for editing a vendor. Triggers the dialog.
     */
    @Override
    public void onEdit(Vendor vendor) {
        showVendorDialog(vendor);
    }

    /**
     * Callback for deleting a vendor. Shows confirmation dialog.
     */
    @Override
    public void onDelete(Vendor vendor) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Vendor")
                .setMessage("Are you sure you want to delete this vendor?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (dbHelper.deleteVendor(vendor.getVendorId())) {
                        Toast.makeText(this, "Vendor deleted", Toast.LENGTH_SHORT).show();
                        loadVendors();
                    } else {
                        Toast.makeText(this, "Failed to delete vendor", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
     * Shows a dialog to select a vendor icon from app resources.
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
                        selectedImageValue = selectedImage.getResourceName();
                        if (activeImagePreview != null) {
                            ProductImageLoader.load(this, activeImagePreview, selectedImageValue, R.drawable.vendor_icon);
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
     * Retrieves relevant drawable resources for vendor icons.
     */
    private List<DrawableImageAdapter.DrawableImage> getDrawableImages() {
        List<DrawableImageAdapter.DrawableImage> drawableImages = new ArrayList<>();
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                // Filter for relevant icons
                if (name.startsWith("product_") || name.startsWith("cat_") || 
                    name.startsWith("vendor_") || name.equals("banner_off") || name.startsWith("category_")) {
                    drawableImages.add(new DrawableImageAdapter.DrawableImage(name, name));
                }
            } catch (Exception ignored) {
            }
        }
        return drawableImages;
    }
}
