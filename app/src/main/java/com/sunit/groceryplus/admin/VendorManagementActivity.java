package com.sunit.groceryplus.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminVendorAdapter;
import com.sunit.groceryplus.models.Vendor;

import java.util.List;

public class VendorManagementActivity extends AppCompatActivity implements AdminVendorAdapter.OnVendorActionListener {

    private RecyclerView recyclerView;
    private AdminVendorAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Vendor> vendorList;

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
    }

    private void loadVendors() {
        vendorList = dbHelper.getAllVendors();
        if (adapter == null) {
            adapter = new AdminVendorAdapter(this, vendorList, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(vendorList);
        }
    }

    private void showVendorDialog(Vendor vendor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_vendor, null);
        builder.setView(view);

        TextView titleTv = view.findViewById(R.id.dialogTitle);
        EditText nameEt = view.findViewById(R.id.vendorNameEt);
        EditText addressEt = view.findViewById(R.id.vendorAddressEt);
        EditText latEt = view.findViewById(R.id.vendorLatEt);
        EditText lngEt = view.findViewById(R.id.vendorLngEt);
        EditText iconEt = view.findViewById(R.id.vendorIconEt);
        EditText ratingEt = view.findViewById(R.id.vendorRatingEt);
        MaterialButton saveBtn = view.findViewById(R.id.saveVendorBtn);

        AlertDialog dialog = builder.create();

        if (vendor != null) {
            titleTv.setText("Edit Vendor");
            nameEt.setText(vendor.getVendorName());
            addressEt.setText(vendor.getAddress());
            latEt.setText(String.valueOf(vendor.getLatitude()));
            lngEt.setText(String.valueOf(vendor.getLongitude()));
            iconEt.setText(vendor.getIcon());
            ratingEt.setText(String.valueOf(vendor.getRating()));
        }

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String address = addressEt.getText().toString().trim();
            String latStr = latEt.getText().toString().trim();
            String lngStr = lngEt.getText().toString().trim();
            String icon = iconEt.getText().toString().trim();
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

    @Override
    public void onEdit(Vendor vendor) {
        showVendorDialog(vendor);
    }

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
}
