package com.sunit.groceryplus.models;

public class DeliveryPerson {
    private int personId;
    private String name;
    private String phone;
    private String status;
    private boolean available;
    private int currentOrderId;

    public DeliveryPerson() {}

    public DeliveryPerson(int personId, String name, String phone, String status, boolean available, int currentOrderId) {
        this.personId = personId;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.available = available;
        this.currentOrderId = currentOrderId;
    }

    // Getters and Setters
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
