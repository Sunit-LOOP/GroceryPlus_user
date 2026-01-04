package com.sunit.groceryplus;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.adapters.NotificationAdapter;


/**
 * NotificationActivity - Displays a list of user notifications.
 * 
 * This activity provides a history of notifications sent to the user, including order updates,
 * promotional messages, and system alerts. It fetches notifications from the database
 * and displays them in a scrollable list.
 * 
 * Key Features:
 * - List view of notifications
 * - Integration with DatabaseHelper for fetching notifications
 * - Simple, clean UI
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationsRv;
    private NotificationAdapter adapter;
    private DatabaseHelper dbHelper;
    private int userId;

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

    private void setupRecyclerView() {
        notificationsRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, null);
        notificationsRv.setAdapter(adapter);
    }

    private void loadNotifications() {
        if (userId != -1) {
            Cursor cursor = dbHelper.getUserNotifications(userId);
            adapter.swapCursor(cursor);
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
