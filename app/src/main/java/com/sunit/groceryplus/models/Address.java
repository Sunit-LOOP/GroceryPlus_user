package com.sunit.groceryplus.models;

/** Address - Model representing a physical delivery address with geolocation data. */
public class Address {
    
    // Unique identifier for the address in the database
    private int addressId;          // Unique DB identifier
    private int userId;             // Owner user ID
    private String type;            // Address type (Home, Work, Other)
    private String fullAddress;     // Complete text address
    private String landmark;        // Nearby landmark (optional)
    private String city;            // City name
    private String area;            // Area/Neighborhood
    private double latitude;        // Geo-location latitude
    private double longitude;       // Geo-location longitude
    private boolean isDefault;      // Primary address flag

    /**
     * Helper methods for compatibility with UI adapters.
     */
    public String getName() { return type; }
    public String getStreetAddress() { return fullAddress; }

    /**
     * Full constructor.
     */
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

    // ================= GETTERS AND SETTERS =================
    // These methods allow accessing and modifying private fields safely

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

    }
