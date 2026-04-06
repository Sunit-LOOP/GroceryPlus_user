package com.sunit.groceryplus.models;

/** SupportTicket - Entity representing a customer support request. */
public class SupportTicket {
    private int ticketId;
    private int userId;
    private int orderId;
    private String subject;
    private String description;
    private String issueType;
    private String status;
    private String priority;
    private String issueImage;
    private String createdAt;
    private String updatedAt;

    public SupportTicket() {}

    public SupportTicket(int ticketId, int userId, int orderId, String subject, String description, 
                        String issueType, String status, String priority, String issueImage, 
                        String createdAt, String updatedAt) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.orderId = orderId;
        this.subject = subject;
        this.description = description;
        this.issueType = issueType;
        this.status = status;
        this.priority = priority;
        this.issueImage = issueImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getIssueImage() { return issueImage; }
    public void setIssueImage(String issueImage) { this.issueImage = issueImage; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
