package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.models.Message;

import java.util.List;

/**
 * Adapter for displaying Instant Messages in a chat conversation.
 * Determines if a message was sent by the current user or received from another,
 * and chooses the appropriate layout alignment.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Message> messages;
    private int currentUserId;

    /**
     * Constructor
     * @param context App context
     * @param messages List of chat messages
     * @param currentUserId ID of logged-in user to determine alignment (Sent vs Received)
     */
    public ChatAdapter(Context context, List<Message> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        // Logic to toggle visibility of Sent vs Received bubbles
        if (message.getSenderId() == currentUserId) {
            // Sent by me: Show Right-aligned bubble
            holder.layoutSent.setVisibility(View.VISIBLE);
            holder.layoutReceived.setVisibility(View.GONE);
            holder.tvSentMessage.setText(message.getMessageText());
            holder.tvSentTime.setText(message.getCreatedAt());
        } else {
            // Received: Show Left-aligned bubble
            holder.layoutSent.setVisibility(View.GONE);
            holder.layoutReceived.setVisibility(View.VISIBLE);
            holder.tvReceivedMessage.setText(message.getMessageText());
            holder.tvReceivedTime.setText(message.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutSent, layoutReceived;
        TextView tvSentMessage, tvSentTime;
        TextView tvReceivedMessage, tvReceivedTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutSent = itemView.findViewById(R.id.layoutSent);
            layoutReceived = itemView.findViewById(R.id.layoutReceived);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentTime = itemView.findViewById(R.id.tvSentTime);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedTime = itemView.findViewById(R.id.tvReceivedTime);
        }
    }
}
