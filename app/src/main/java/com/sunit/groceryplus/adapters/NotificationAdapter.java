package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.DatabaseContract;
import com.sunit.groceryplus.OrderTrackingActivity;
import com.sunit.groceryplus.R;

/** NotificationAdapter - Displays system notifications (Titles, messages, and timestamps). */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    /** Constructor. */
    public NotificationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    /** Swaps the old cursor with a new one (Replaces old data source). */
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
        String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NotificationEntry.COLUMN_NAME_TYPE));
        final String refId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NotificationEntry.COLUMN_NAME_REF_ID));

        holder.title.setText(title);
        holder.message.setText(message);
        holder.timestamp.setText(timestamp);

        // Set Icon based on Type
        int iconRes = R.drawable.ic_notifications;
        if (type != null) {
            switch (type) {
                case "ORDER":
                case "DELIVERY":
                    iconRes = R.drawable.order_icon;
                    break;
                case "PAYMENT":
                    iconRes = R.drawable.card_icon;
                    break;
                case "PROMO":
                    iconRes = R.drawable.promo_icon;
                    break;
                case "STOCK":
                    iconRes = R.drawable.product_icon;
                    break;
            }
        }
        holder.icon.setImageResource(iconRes);

        // Click Logic
        holder.itemView.setOnClickListener(v -> {
            if (("ORDER".equals(type) || "DELIVERY".equals(type)) && refId != null) {
                try {
                    Intent intent = new Intent(context, OrderTrackingActivity.class);
                    intent.putExtra("order_id", Integer.parseInt(refId));
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e("NotificationAdapter", "Error opening order tracking", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    /** ViewHolder for notification row components. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        TextView title, message, timestamp;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            timestamp = itemView.findViewById(R.id.notificationTimestamp);
            icon = itemView.findViewById(R.id.notificationIcon);
        }
    }
}
