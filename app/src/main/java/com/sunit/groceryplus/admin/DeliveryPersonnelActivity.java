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
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.DeliveryPersonnelAdapter;
import com.sunit.groceryplus.models.DeliveryPerson;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonnelActivity extends AppCompatActivity {

    private RecyclerView personnelRv;
    private FloatingActionButton addPersonFab;
    private DeliveryPersonnelAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_personnel);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        
        personnelRv = findViewById(R.id.personnelRv);
        addPersonFab = findViewById(R.id.addPersonFab);

        setupRecyclerView();
        loadPersonnel();

        addPersonFab.setOnClickListener(v -> showAddPersonDialog());
    }

    private void setupRecyclerView() {
        personnelRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryPersonnelAdapter(this, new ArrayList<>(), new DeliveryPersonnelAdapter.OnStatusToggleListener() {
            @Override
            public void onToggleStatus(DeliveryPerson person) {
                togglePersonStatus(person);
            }
        });
        personnelRv.setAdapter(adapter);
    }

    private void loadPersonnel() {
        List<DeliveryPerson> personnel = new ArrayList<>();
        Cursor cursor = dbHelper.getAllDeliveryPersonnel();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS));
                
                personnel.add(new DeliveryPerson(id, name, phone, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        adapter.updateList(personnel);
    }

    private void showAddPersonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_delivery_person, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextInputEditText nameEt = dialogView.findViewById(R.id.personNameEt);
        TextInputEditText phoneEt = dialogView.findViewById(R.id.personPhoneEt);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);
        Button saveBtn = dialogView.findViewById(R.id.saveBtn);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        saveBtn.setOnClickListener(v -> {
            String name = nameEt.getText().toString().trim();
            String phone = phoneEt.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.addDeliveryPerson(name, phone, "Available");
            if (result != -1) {
                Toast.makeText(this, "Person added", Toast.LENGTH_SHORT).show();
                loadPersonnel();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error adding person", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void togglePersonStatus(DeliveryPerson person) {
        String newStatus = "Available".equalsIgnoreCase(person.getStatus()) ? "Busy" : "Available";
        if (dbHelper.updateDeliveryPersonStatus(person.getPersonId(), newStatus)) {
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            loadPersonnel();
        } else {
            Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show();
        }
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
