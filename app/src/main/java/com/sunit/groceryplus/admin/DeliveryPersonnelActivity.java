package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.DeliveryPersonRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.DeliveryPersonnelAdapter;
import com.sunit.groceryplus.models.DeliveryPerson;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelActivity extends AppCompatActivity {

    private RecyclerView personnelRv;
    private FloatingActionButton addPersonFab;
    private DeliveryPersonnelAdapter adapter;
    private DeliveryPersonRepository repository;
    private List<DeliveryPerson> personnelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_personnel);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Delivery Personnel");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        repository = new DeliveryPersonRepository(this);
        personnelRv = findViewById(R.id.personnelRv);
        addPersonFab = findViewById(R.id.addPersonFab);

        personnelRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryPersonnelAdapter(this, new ArrayList<>(), this::onToggleAvailability);
        personnelRv.setAdapter(adapter);

        addPersonFab.setOnClickListener(v -> showAddDialog());

        loadData();
    }

    private void loadData() {
        personnelList = repository.getAllDeliveryPersonnel();
        adapter.updateList(personnelList);
    }

    private void onToggleAvailability(DeliveryPerson person) {
        boolean newAvailability = !person.isAvailable();
        boolean updated = repository.setAvailability(person.getPersonId(), newAvailability);
        if (updated) {
            Toast.makeText(this, person.getName() + " is now " + (newAvailability ? "Available" : "Unavailable"), Toast.LENGTH_SHORT).show();
            loadData();
        } else {
            Toast.makeText(this, "Failed to update availability", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddDialog() {
        showDeliveryPersonDialog(null);
    }

    private void showDeliveryPersonDialog(DeliveryPerson person) {
        boolean isEdit = person != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_delivery_person, null);
        builder.setView(dialogView);

        // TODO: Add these fields to dialog_add_delivery_person layout
        // TextInputEditText nameEt = dialogView.findViewById(R.id.nameEt);
        // TextInputEditText phoneEt = dialogView.findViewById(R.id.phoneEt);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        if (isEdit) {
            // nameEt.setText(person.getName());
            // phoneEt.setText(person.getPhone());
        }

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(v -> {
            // TODO: Uncomment when EditText fields are added to layout
            // String name = nameEt.getText().toString().trim();
            // String phone = phoneEt.getText().toString().trim();
            String name = ""; // Placeholder
            String phone = ""; // Placeholder

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                boolean updated = repository.updateDeliveryPerson(person.getPersonId(), name, phone);
                if (updated) {
                    Toast.makeText(this, "Delivery person updated", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
                }
            } else {
                long id = repository.addDeliveryPerson(name, phone);
                if (id != -1) {
                    Toast.makeText(this, "Delivery person added", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
