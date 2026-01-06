package com.sunit.groceryplus.models;

/** DeliveryPerson - Model representing a fleet staff member and their availability. */
public class DeliveryPerson {
    
    // Unique ID for the delivery person
    private int personId;       // Unique DB identifier
    private String name;        // Staff Name
    private String phone;       // Contact Number
    private String status;      // Employment Status (Active/Inactive)
    private boolean available;  // Availability Flag
    private int currentOrderId; // Currently assigned order ID (0 if none)

    /** Default Constructor. */
    public DeliveryPerson() {}

    /**
     * Full Constructor.
     */
    public DeliveryPerson(int personId, String name, String phone, String status, boolean available, int currentOrderId) {
        this.personId = personId;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.available = available;
        this.currentOrderId = currentOrderId;
    }

    // ================= GETTERS AND SETTERS =================

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(int currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
}
