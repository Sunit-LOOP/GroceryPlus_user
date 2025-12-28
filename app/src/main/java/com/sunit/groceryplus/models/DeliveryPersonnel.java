package com.sunit.groceryplus.models;

public class DeliveryPersonnel {
    private int deliveryPersonId;
    private String name;
    private String email;
    private String phone;
    private String vehicleType;
    private String licenseNumber;
    private boolean isAvailable;
    private String currentLocation;
    private double rating;
    private int totalDeliveries;
    private String createdAt;
    private String updatedAt;

    public DeliveryPersonnel() {}

    public DeliveryPersonnel(int deliveryPersonId, String name, String email, String phone, String vehicleType) {
        this.deliveryPersonId = deliveryPersonId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.isAvailable = true; // Default from schema
        this.rating = 5.0; // Default from schema
        this.totalDeliveries = 0; // Default from schema
    }

    // Getters and Setters
    public int getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(int deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalDeliveries() { return totalDeliveries; }
    public void setTotalDeliveries(int totalDeliveries) { this.totalDeliveries = totalDeliveries; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}