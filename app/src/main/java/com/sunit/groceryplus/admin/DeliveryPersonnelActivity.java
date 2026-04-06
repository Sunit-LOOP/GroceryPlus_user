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


/** DeliveryPersonnelActivity - Admin interface for recruiting, updating, and managing availability of delivery staff (fleet management). */
public class DeliveryPersonnelActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView personnelRv;
    private FloatingActionButton addPersonFab;
    
    // Data & Adapters
    private DeliveryPersonnelAdapter adapter;
    private DeliveryPersonRepository repository;
    private List<DeliveryPerson> personnelList;

    /**
     * Initializes activity, sets up toolbar, recycler view and floating action button.
     * @param savedInstanceState Saved instance state
     */
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

    /**
     * Refreshes the list of delivery personnel from the repository.
     */
    private void loadData() {
        personnelList = repository.getAllDeliveryPersonnel();
        adapter.updateList(personnelList);
    }

    /**
     * Toggles the availability status of a delivery person.
     * Callback for adapter item clicks.
     * @param person The delivery person to update.
     */
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

    /**
     * wrapper for showing dialog for adding new person.
     */
    private void showAddDialog() {
        showDeliveryPersonDialog(null);
    }

    /**
     * Displays a dialog to add a new delivery person or edit an existing one.
     * @param person The person object to edit, or null for creating new.
     */
    private void showDeliveryPersonDialog(DeliveryPerson person) {
        boolean isEdit = person != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_delivery_person, null);
        builder.setView(dialogView);

        TextInputEditText nameEt = dialogView.findViewById(R.id.personNameEt);
        TextInputEditText phoneEt = dialogView.findViewById(R.id.personPhoneEt);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        if (isEdit) {
            nameEt.setText(person.getName());
            phoneEt.setText(person.getPhone());
        }

        AlertDialog dialog = builder.create();

        saveBtn.setOnClickListener(v -> {
            // Get actual user input from EditText fields
            String name = nameEt.getText().toString().trim();
            String phone = phoneEt.getText().toString().trim();

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

    /**
     * Handles toolbar menu actions.
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
