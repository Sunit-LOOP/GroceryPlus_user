package com.sunit.groceryplus.admin;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.UserRepository;
import com.sunit.groceryplus.adapters.AdminCustomerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * CustomerManagementActivity - Admin interface for viewing registered customers.
 * 
 * This activity displays a list of all registered users (customers) in the system.
 * It provides visibility into the user base but currently operates in a read-only mode regarding user profiles.
 * 
 * Key Features:
 * - List all registered customers
 * - View customer basic details (Name, Email, Phone)
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class CustomerManagementActivity extends AppCompatActivity {

    private RecyclerView customersRv;
    private AdminCustomerAdapter adapter;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_management);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        customersRv = findViewById(R.id.customersRv);
        userRepository = new UserRepository(this);

        setupRecyclerView();
        loadCustomers();
    }

    private void setupRecyclerView() {
        customersRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCustomerAdapter(this, new ArrayList<>());
        customersRv.setAdapter(adapter);
    }

    private void loadCustomers() {
        List<User> users = userRepository.getAllUsers();
        adapter.updateUsers(users);
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
