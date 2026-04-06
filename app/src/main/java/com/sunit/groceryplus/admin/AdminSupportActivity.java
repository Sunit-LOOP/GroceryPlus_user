package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.OrderRepository;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminSupportAdapter;
import com.sunit.groceryplus.models.SupportTicket;
import com.sunit.groceryplus.utils.GroceryNotificationManager;

import java.util.ArrayList;
import java.util.List;

/** AdminSupportActivity - Dashboard for admins to handle customer reports and process refunds. */
public class AdminSupportActivity extends AppCompatActivity {

    private RecyclerView ticketsRv;
    private TextView noTicketsTv;
    private DatabaseHelper dbHelper;
    private OrderRepository orderRepository;
    private AdminSupportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_support);

        dbHelper = new DatabaseHelper(this);
        orderRepository = new OrderRepository(this);
        initViews();
        loadTickets();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ticketsRv = findViewById(R.id.ticketsRv);
        noTicketsTv = findViewById(R.id.noTicketsTv);
        ticketsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadTickets() {
        List<SupportTicket> tickets = new ArrayList<>();
        Cursor cursor = dbHelper.getAllTickets();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SupportTicket ticket = new SupportTicket();
                ticket.setTicketId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry._ID)));
                ticket.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_USER_ID)));
                ticket.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ORDER_ID)));
                ticket.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_SUBJECT)));
                ticket.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_DESCRIPTION)));
                ticket.setIssueType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ISSUE_TYPE)));
                ticket.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_STATUS)));
                ticket.setIssueImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_ISSUE_IMAGE)));
                ticket.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SupportTicketEntry.COLUMN_NAME_CREATED_AT)));
                tickets.add(ticket);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (tickets.isEmpty()) {
            noTicketsTv.setVisibility(View.VISIBLE);
            ticketsRv.setVisibility(View.GONE);
        } else {
            noTicketsTv.setVisibility(View.GONE);
            ticketsRv.setVisibility(View.VISIBLE);
            setupAdapter(tickets);
        }
    }

    private void setupAdapter(List<SupportTicket> tickets) {
        adapter = new AdminSupportAdapter(this, tickets, new AdminSupportAdapter.OnTicketActionListener() {
            @Override
            public void onResolveTicket(SupportTicket ticket) {
                dbHelper.updateTicketStatus(ticket.getTicketId(), "resolved");
                Toast.makeText(AdminSupportActivity.this, "Ticket resolved", Toast.LENGTH_SHORT).show();
                loadTickets();
                
                GroceryNotificationManager.getInstance(AdminSupportActivity.this).sendNotification(
                        ticket.getUserId(), "Support Update", "Your ticket #" + ticket.getTicketId() + " has been resolved.",
                        GroceryNotificationManager.TYPE_ACCOUNT, "");
            }

            @Override
            public void onApproveWalletRefund(SupportTicket ticket, double amount) {
                // Update User Wallet
                dbHelper.updateWalletBalance(ticket.getUserId(), amount);
                
                // Update ticket
                dbHelper.updateTicketStatus(ticket.getTicketId(), "resolved (refunded to wallet)");
                
                Toast.makeText(AdminSupportActivity.this, "Refund of Rs. " + amount + " processed to wallet", Toast.LENGTH_LONG).show();
                loadTickets();
                
                // Notify User
                GroceryNotificationManager.getInstance(AdminSupportActivity.this).sendNotification(
                        ticket.getUserId(), "Refund Processed", "A refund of Rs. " + amount + " has been added to your wallet.",
                        GroceryNotificationManager.TYPE_PAYMENT, String.valueOf(ticket.getOrderId()));
            }
        });
        ticketsRv.setAdapter(adapter);
    }
}
