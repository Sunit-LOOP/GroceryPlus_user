package com.sunit.groceryplus.models;

/**
 * Vendor Model Class
 * 
 * Represents a store or supplier of products.
 * Includes location details and performance rating.
 */
public class Vendor {
    
    // Unique ID for the vendor
    private int vendorId;
    
    // Display Name
    private String vendorName;
    
    // Physical Address
    private String address;
    
    // Geo-location for map
    private double latitude;
    private double longitude;
    
    // Icon/Logo resource
    private String icon;
    
    // Average rating
    private double rating;

    /**
     * Full Constructor
     * 
     * @param vendorId Unique ID
     * @param vendorName Name
     * @param address Text Address
     * @param latitude Geo-Lat
     * @param longitude Geo-Long
     * @param icon Icon URL/Asset
     * @param rating Rating (0-5)
     */
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
