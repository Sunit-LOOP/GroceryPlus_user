package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.R;

/**
 * Adapter for displaying System Notifications to the user.
 * Uses CursorAdapter approach (RecyclerView implementation) for efficient DB loading.
 * Shows title, message, and timestamp of notifications.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public NotificationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    /**
     * Swaps the old cursor with a new one when data changes.
     */
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        this.cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NotificationEntry.COLUMN_NAME_TITLE));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NotificationEntry.COLUMN_NAME_MESSAGE));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NotificationEntry.COLUMN_NAME_CREATED_AT));

        holder.title.setText(title);
        holder.message.setText(message);
        holder.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            timestamp = itemView.findViewById(R.id.notificationTimestamp);
        }
    }
}
