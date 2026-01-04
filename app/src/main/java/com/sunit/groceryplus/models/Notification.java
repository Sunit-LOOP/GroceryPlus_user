package com.sunit.groceryplus.models;

/**
 * Notification Model Class
 * 
 * Represents a system generated notification for the user.
 * Used to alert users about order changes, promotions, or system events.
 */
public class Notification {
    
    // Unique ID for the notification
    private int notificationId;
    
    // The recipient user's ID
    private int userId;
    
    // Short title of the notification (e.g., "Order Shipped")
    private String title;
    
    // Detailed body text of the notification
    private String message;
    
    // Timestamp when the notification was created
    private String createdAt;
    
    // Status to track if the user has seen this notification
    private boolean isRead;

    /**
     * Full Constructor
     * 
     * @param notificationId Unique ID
     * @param userId Recipient ID
     * @param title Title text
     * @param message Message body
     * @param createdAt Time string
     * @param isRead Read status
     */
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
