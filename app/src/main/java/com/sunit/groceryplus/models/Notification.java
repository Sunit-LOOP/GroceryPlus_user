package com.sunit.groceryplus.models;

/** Notification - Model representing a system alert or update for a user. */
public class Notification {
    
    private int notificationId; // Unique DB identifier
    private int userId;         // Recipient User ID
    private String title;       // Notification Heading
    private String message;     // Notification Body Text
    private String createdAt;   // Timestamp
    private boolean isRead;     // Read Status

    /** Full Constructor. */
    public Notification(int notificationId, int userId, String title, String message, String createdAt, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // ================= GETTERS AND SETTERS =================

    public int getNotificationId() { return notificationId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
