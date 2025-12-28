package com.sunit.groceryplus.models;

public class Address {
    private int addressId;
    private int userId;
    private String type;
    private String fullAddress;
    private String landmark;
    private String city;
    private String area;
    private double latitude;
    private double longitude;
    private boolean isDefault;

    public Address(int addressId, int userId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        this.addressId = addressId;
        this.userId = userId;
        this.type = type;
        this.fullAddress = fullAddress;
        this.landmark = landmark;
        this.city = city;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = isDefault;
    }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }

    // Compatibility methods for PaymentActivity
    public String getName() { return type; }
    public String getStreetAddress() { return fullAddress; }
}
