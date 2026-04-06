package com.sunit.groceryplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.adapters.SupportTicketAdapter;
import com.sunit.groceryplus.models.SupportTicket;

import java.util.List;

/** SupportCenterActivity - Central hub for user support, tickets, and contact options. */
public class SupportCenterActivity extends AppCompatActivity {

    private int userId;
    private OrderRepository orderRepository;
    private RecyclerView ticketsRecyclerView;
    private TextView emptyTicketsTv;
    private SupportTicketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_center);

        userId = getIntent().getIntExtra("user_id", -1);
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

        ticketsRecyclerView = findViewById(R.id.ticketsRecyclerView);
        emptyTicketsTv = findViewById(R.id.emptyTicketsTv);

        findViewById(R.id.chatCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        findViewById(R.id.callCard).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:9815689963")); // Example number
            startActivity(intent);
        });
    }

    private void loadTickets() {
        List<SupportTicket> tickets = orderRepository.getUserTickets(userId);
        if (tickets.isEmpty()) {
            emptyTicketsTv.setVisibility(View.VISIBLE);
            ticketsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyTicketsTv.setVisibility(View.GONE);
            ticketsRecyclerView.setVisibility(View.VISIBLE);
            setupRecyclerView(tickets);
        }
    }

    private void setupRecyclerView(List<SupportTicket> tickets) {
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupportTicketAdapter(this, tickets);
        ticketsRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
