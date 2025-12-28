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

    private void loadMessages() {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = dbHelper.getAllMessages();
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID));
                int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID));
                int receiverId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_RECEIVER_ID));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT));
                int isRead = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_IS_READ));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT));
                
                String senderName = "";
                int senderNameIdx = cursor.getColumnIndex("sender_name");
                if (senderNameIdx != -1) senderName = cursor.getString(senderNameIdx);

                messages.add(new Message(id, senderId, receiverId, text, isRead == 1, date, senderName, null));
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
