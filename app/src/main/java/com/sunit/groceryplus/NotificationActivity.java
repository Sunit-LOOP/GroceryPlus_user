package com.sunit.groceryplus;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.adapters.NotificationAdapter;


/** NotificationActivity - Scrollable history of system, order, and promotional alerts for the user. */
public class NotificationActivity extends AppCompatActivity {

    // Infrastructure & Data
    private RecyclerView notificationsRv;
    private NotificationAdapter adapter;
    private DatabaseHelper dbHelper;
    private int userId;

    /** Initializes the notifications list and toolbar. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        userId = getIntent().getIntExtra("user_id", -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        notificationsRv = findViewById(R.id.notificationsRv);

        setupRecyclerView();
        loadNotifications();
    }

    /** Configures the notification list with its adapter. */
    private void setupRecyclerView() {
        notificationsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, null);
        notificationsRv.setAdapter(adapter);
    }

    /** Fetches the latest notifications from the local database. */
    private void loadNotifications() {
        if (userId != -1) {
            Cursor cursor = dbHelper.getUserNotifications(userId);
            adapter.swapCursor(cursor);
        }
    }

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
