package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.R;

public class AdminChatAdapter extends RecyclerView.Adapter<AdminChatAdapter.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private Cursor cursor;
    private int adminId;

    public AdminChatAdapter(Context context, Cursor cursor, int adminId) {
        this.context = context;
        this.cursor = cursor;
        this.adminId = adminId;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        this.cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!cursor.moveToPosition(position)) {
            return -1;
        }
        int senderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID));
        return senderId == adminId ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_received, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_CREATED_AT));

        holder.message.setText(message);
        holder.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chatMessageTv);
            timestamp = itemView.findViewById(R.id.chatTimestampTv);
        }
    }
}
