package com.sunit.groceryplus.models;

public class Notification {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String createdAt;
    private boolean isRead;

    public Notification(int notificationId, int userId, String title, String message, String createdAt, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getNotificationId() { return notificationId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { isRead = read; }
}
