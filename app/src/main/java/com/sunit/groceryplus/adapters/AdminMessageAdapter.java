package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.admin.AdminChatActivity;
import com.sunit.groceryplus.models.Message;

import java.util.List;

public class AdminMessageAdapter extends RecyclerView.Adapter<AdminMessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;
    private int adminId;

    public AdminMessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.adminId = new com.sunit.groceryplus.DatabaseHelper(context).getAdminId();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_admin_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        
        // The senderName field was populated with the partner's name in AdminMessagesActivity
        holder.senderTv.setText(message.getSenderName() != null ? message.getSenderName() : "User #" + message.getSenderId());
        holder.dateTv.setText(message.getCreatedAt());
        
        String prefix = (message.getSenderId() == adminId) ? "You: " : "";
        holder.contentTv.setText(prefix + message.getMessageText());

        // Bold unread messages (received by admin and not read)
        boolean isUnread = !message.isRead() && message.getReceiverId() == adminId;
        if (isUnread) {
            holder.senderTv.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.contentTv.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.contentTv.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.senderTv.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.contentTv.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.contentTv.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        holder.itemView.setOnClickListener(v -> {
            int partnerId = (message.getSenderId() == adminId) ? message.getReceiverId() : message.getSenderId();
            Intent intent = new Intent(context, AdminChatActivity.class);
            intent.putExtra("user_id", partnerId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderTv, dateTv, contentTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTv = itemView.findViewById(R.id.msgSenderTv);
            dateTv = itemView.findViewById(R.id.msgDateTv);
            contentTv = itemView.findViewById(R.id.msgContentTv);
        }
    }
}
