package com.sunit.groceryplus.models;

/**
 * Address Model Class
 * 
 * This class represents a physical delivery address associated with a user.
 * It stores all the necessary details required to locate a user for delivery,
 * including both the textual address and the geographical coordinates (latitude/longitude)
 * for map integration.
 */
public class Address {
    
    // Unique identifier for the address in the database
    private int addressId;
    
    // The ID of the user who owns this address
    private int userId;
    
    // The type of address (e.g., "Home", "Work", "Other")
    private String type;
    
    // The full text address (e.g., "123 Main St, Apartment 4B")
    private String fullAddress;
    
    // A nearby landmark to help delivery persons find the location (e.g., "Near Central Park")
    private String landmark;
    
    // The city name
    private String city;
    
    // The specific area or neighborhood
    private String area;
    
    // Geographical latitude for map positioning
    private double latitude;
    
    // Geographical longitude for map positioning
    private double longitude;
    
    // Flag to indicate if this is the user's default delivery address
    private boolean isDefault;

    /**
     * Constructor to initialize an Address object with all details.
     * 
     * @param addressId Unique ID
     * @param userId User's ID
     * @param type Type of address (Home/Work)
     * @param fullAddress Complete text address
     * @param landmark Nearby landmark
     * @param city City name
     * @param area Area/Neighborhood
     * @param latitude Geo-lat
     * @param longitude Geo-long
     * @param isDefault Is this the default address?
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

    // ================= COMPATIBILITY METHODS =================
    // These methods provide alternate names for fields, useful for
    // adapters or legacy code that expects standard naming conventions.

    /**
     * Returns the name/type of the address (e.g., "Home").
     * Used by PaymentActivity for display.
     */
    public String getName() { return type; }

    /**
     * Returns the street address.
     * Used by PaymentActivity for display.
     */
    public String getStreetAddress() { return fullAddress; }
}
