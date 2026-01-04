package com.sunit.groceryplus.models;

/**
 * DeliveryPerson Model Class
 * 
 * Represents a delivery personnel in the system.
 * This class tracks the person's availability, contact info,
 * and current assignment status.
 */
public class DeliveryPerson {
    
    // Unique ID for the delivery person
    private int personId;
    
    // Full Name
    private String name;
    
    // Contact Phone Number
    private String phone;
    
    // Employment Status (Active/Inactive)
    private String status;
    
    // Availability for new orders (True = Can accept orders)
    private boolean available;
    
    // ID of the order currently being delivered (0 if none)
    private int currentOrderId;

    /**
     * Default Constructor
     */
    public DeliveryPerson() {}

    /**
     * Full Constructor
     * 
     * @param personId Unique ID
     * @param name Name
     * @param phone Contact Number
     * @param status Employment Status
     * @param available Is currently available?
     * @param currentOrderId ID of active order
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
