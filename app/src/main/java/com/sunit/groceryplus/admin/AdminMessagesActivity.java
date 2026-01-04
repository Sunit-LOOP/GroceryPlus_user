package com.sunit.groceryplus.admin;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.R;
import com.sunit.groceryplus.adapters.AdminMessageAdapter;
import com.sunit.groceryplus.models.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * AdminMessagesActivity - Inbox for customer support.
 * 
 * This activity lists all users who have initiated a chat with the admin.
 * It acts as an inbox, displaying the latest message from each conversation,
 * allowing the admin to select a conversation to view details.
 * 
 * Key Features:
 * - List of conversations
 * - Status indicators (Read/Unread)
 * - Navigation to individual chats
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class AdminMessagesActivity extends AppCompatActivity {

    private RecyclerView messagesRv;
    private AdminMessageAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_messages);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        messagesRv = findViewById(R.id.messagesRv);

        setupRecyclerView();
        loadMessages();
    }

    private void setupRecyclerView() {
        messagesRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminMessageAdapter(this, new ArrayList<>());
        messagesRv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }

    private void loadMessages() {
        int adminId = dbHelper.getAdminId();
        List<Message> messages = new ArrayList<>();
        Cursor cursor = dbHelper.getConversations(adminId);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID));
                int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID));
                int receiverId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT));
                int isRead = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_IS_READ));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT));
                
                String partnerName = "";
                int nameIdx = cursor.getColumnIndex("remote_name");
                if (nameIdx != -1) partnerName = cursor.getString(nameIdx);
                
                // Construct the message preserving the original sender/receiver
                Message msg = new Message(id, senderId, receiverId, text, isRead == 1, date, partnerName, null);
                messages.add(msg);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        adapter.updateMessages(messages);
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
