package com.sunit.groceryplus;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.sunit.groceryplus.adapters.ChatAdapter;
import com.sunit.groceryplus.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    private TextInputEditText messageEditText;
    private FloatingActionButton sendButton;
    private RecyclerView chatRv;
    
    private DatabaseHelper dbHelper;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    
    private int userId;
    private int adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get User ID
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login to chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        adminId = dbHelper.getAdminId();

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load Messages
        loadMessages();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatRv = findViewById(R.id.chatRv);

        // Setup Bottom Navigation
        com.sunit.groceryplus.utils.NavigationHelper.setupNavigation(this, userId);
        
        MaterialToolbar toolbar = findViewById(R.id.messageToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Scroll to bottom
        chatRv.setLayoutManager(layoutManager);
        
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, userId);
        chatRv.setAdapter(chatAdapter);
    }

    private void loadMessages() {
        messageList.clear();
        Cursor cursor = dbHelper.getConversation(userId, adminId);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID));
                int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID));
                int receiverId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT));
                
                messageList.add(new Message(id, senderId, receiverId, text, false, date));
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        chatAdapter.notifyDataSetChanged();
        if (!messageList.isEmpty()) {
            chatRv.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void setClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        
        if (text.isEmpty()) {
            return;
        }
        
        long result = dbHelper.sendMessage(userId, adminId, text);
        
        if (result != -1) {
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Message newMessage = new Message((int)result, userId, adminId, text, false, currentTime);
            chatAdapter.addMessage(newMessage);
            chatRv.smoothScrollToPosition(messageList.size() - 1);
            messageEditText.setText("");
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}