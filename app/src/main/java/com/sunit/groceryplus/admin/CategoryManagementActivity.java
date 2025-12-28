package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.CategoryRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminCategoryAdapter;
import com.sunit.groceryplus.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {

    private RecyclerView categoriesRv;
    private FloatingActionButton addCategoryFab;
    private AdminCategoryAdapter adapter;
    private CategoryRepository categoryRepository;

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
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        // Populate if editing
        if (category != null) {
            nameEt.setText(category.getCategoryName());
            descriptionEt.setText(category.getCategoryDescription());
        }

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String description = descriptionEt.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Category name required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (category == null) {
                // Add
                success = categoryRepository.addCategory(name, description);
                if (success) {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Update
                success = categoryRepository.updateCategory(category.getCategoryId(), name, description);
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
