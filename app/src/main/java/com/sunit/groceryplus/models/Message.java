package com.sunit.groceryplus.models;

public class Message {
    private int messageId;
    private int senderId;
    private int receiverId;
    private String messageText;
    private boolean isRead;
    private String createdAt;
    
    // For display purposes
    private String senderName;
    private String receiverName;

    // Constructor for creating new message
    public Message(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = false;
    }

    // Constructor for retrieving from database
    public Message(int messageId, int senderId, int receiverId, String messageText, boolean isRead, String createdAt) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Full constructor with names
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

    // Getters
    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    // Setters
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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
