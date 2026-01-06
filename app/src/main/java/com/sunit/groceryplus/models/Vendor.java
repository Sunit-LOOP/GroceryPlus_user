package com.sunit.groceryplus.models;

/** Vendor - Model representing a store or supplier entity, including location data. */
public class Vendor {
    
    private int vendorId;       // Unique DB identifier
    private String vendorName;  // Display Name
    private String address;     // Physical Address
    private double latitude;    // Geo-location Lat
    private double longitude;   // Geo-location Lng
    private String icon;        // Logo Resource/URL
    private double rating;      // Average Rating (0-5)

    /** Full Constructor. */
    public Vendor(int vendorId, String vendorName, String address, double latitude, double longitude, String icon, double rating) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = icon;
        this.rating = rating;
    }

    // ================= GETTERS AND SETTERS =================

    public int getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getIcon() { return icon; }
    public double getRating() { return rating; }

    public void setVendorId(int vendorId) { this.vendorId = vendorId; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setRating(double rating) { this.rating = rating; }
}
