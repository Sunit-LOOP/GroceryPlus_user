package com.sunit.groceryplus.models;

/**
 * Message Model Class
 * 
 * Represents a chat message between a user and an admin.
 * Used for order support, notifications, and general inquiries.
 */
public class Message {
    
    // Unique ID for the message
    private int messageId;
    
    // ID of the sender (User or Admin)
    private int senderId;
    
    // ID of the receiver
    private int receiverId;
    
    // The content of the message
    private String messageText;
    
    // Has the message been read by the receiver?
    private boolean isRead;
    
    // Timestamp of creation
    private String createdAt;
    
    // -- Display Fields (Not always stored in DB table directly) --
    private String senderName;
    private String receiverName;

    /**
     * Constructor for creating a NEW message to be sent.
     * 
     * @param senderId Sender's ID
     * @param receiverId Receiver's ID
     * @param messageText Content
     */
    public Message(int senderId, int receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.isRead = false; // Default to unread
    }

    /**
     * Constructor for retrieving an EXISTING message from the database.
     * 
     * @param messageId Unique ID
     * @param senderId Sender
     * @param receiverId Receiver
     * @param messageText Content
     * @param isRead Read status
     * @param createdAt Timestamp
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
     * Full Constructor including sender/receiver names.
     * Used for UI display where names are needed (e.g., Admin Chat List).
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
