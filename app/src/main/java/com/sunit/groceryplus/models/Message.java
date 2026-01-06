package com.sunit.groceryplus.models;

/** Message - Model representing a chat message between users and admins. */
public class Message {
    
    // Core Fields
    private int messageId;      // Unique DB identifier
    private int senderId;       // User ID of the sender
    private int receiverId;     // User ID of the receiver
    private String messageText; // Content text
    private boolean isRead;     // Read receipt status
    private String createdAt;   // Timestamp
    
    // UI Helper Fields (Joined Data)
    private String senderName;
    private String receiverName;

    /**
     * Constructor for creating a NEW message (before saving to DB).
     */
    public Message(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = false; // Default to unread
    }

    /**
     * Constructor for retrieving EXISTING messages from DB.
     */
    public Message(int messageId, int senderId, int receiverId, String messageText, boolean isRead, String createdAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    /**
     * Full Constructor including sender/receiver names for list display.
     */
    public Message(int messageId, int senderId, int receiverId, String messageText, boolean isRead, String createdAt, String senderName, String receiverName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    // ================= GETTERS AND SETTERS =================

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageText='" + messageText + '\'' +
                ", isRead=" + isRead +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
