package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminChatAdapter;
import com.sunit.groceryplus.network.ApiClient;
import com.sunit.groceryplus.network.GroceryApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView chatRv;
    private EditText messageEt;
    private ImageButton sendBtn;

    private AdminChatAdapter adapter;
    private DatabaseHelper dbHelper;
    private GroceryApi groceryApi;

    private int userId;
    private int adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);

        userId = getIntent().getIntExtra("user_id", -1);

        dbHelper = new DatabaseHelper(this);
        adminId = dbHelper.getAdminId();
        groceryApi = ApiClient.getGroceryApi();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        chatRv = findViewById(R.id.chatRv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        setupRecyclerView();
        loadConversation();

        sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        chatRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminChatAdapter(this, null, adminId);
        chatRv.setAdapter(adapter);
    }

    private void loadConversation() {
        if (userId != -1) {
            Cursor cursor = dbHelper.getConversation(adminId, userId);
            adapter.swapCursor(cursor);
            chatRv.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void sendMessage() {
        String messageText = messageEt.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        if (userId != -1) {
            long result = dbHelper.sendMessage(adminId, userId, messageText);
            if (result != -1) {
                messageEt.setText("");
                loadConversation();

                // Sync message to web API
                syncMessageToAPI(messageText);
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void syncMessageToAPI(String messageText) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender_id", adminId);
        messageData.put("receiver_id", userId);
        messageData.put("message", messageText);

        groceryApi.sendMessage(messageData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Log.d("AdminChat", "Message synced to web API");
                } else {
                    Log.e("AdminChat", "Failed to sync message: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("AdminChat", "Error syncing message to API", t);
            }
        });
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
