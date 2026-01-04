package com.sunit.groceryplus.models;

/**
 * AdminSettings Model Class
 * 
 * Stores global configuration for the application.
 * These settings are managed by the Admin and affect app behavior,
 * such as store info, taxes, delivery fees, and feature toggles.
 */
public class AdminSettings {
    
    // Unique ID (Usually 1 as there is only one settings record)
    private int id;
    
    // -- Store Identity --
    private String storeName;
    private String storeEmail;
    private String storePhone;
    
    // -- Store Location --
    private String storeAddress;
    private String storeCity;
    private String storeState;
    private String storePostalCode;
    private String storeCountry;
    
    // -- Financial Settings --
    private double taxRate;         // Percentage tax applied
    private double deliveryFee;     // Base delivery fee
    private boolean freeDeliveryAbove; // Toggle for free delivery threshold
    private double freeDeliveryThreshold; // Amount above which delivery is free
    private String currencySymbol;  // e.g., "₹", "$"
    
    // -- System Settings --
    private String timezone;
    private boolean enableNotifications;
    private boolean enableEmailNotifications;
    
    // -- Email Server (SMTP) Settings --
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    
    // -- Payment Gateway (Stripe) Settings --
    private boolean stripeEnabled;
    private String stripePublishableKey;
    private String stripeSecretKey;
    private boolean codEnabled;     // Toggle Cash on Delivery
    
    // -- Operational Info --
    private String businessHours;
    private String supportEmail;
    private String supportPhone;
    
    // -- Branding --
    private String logoUrl;
    private String faviconUrl;
    private String primaryColor;
    private String accentColor;
    
    // -- Maintenance --
    private boolean maintenanceMode; // If true, app shows maintenance screen
    private String maintenanceMessage; 
    
    // Timestamps
    private String createdAt;
    private String updatedAt;

    public AdminSettings() {}

    // ================= GETTERS AND SETTERS =================
    // Standard accessors for all configuration fields

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getStoreEmail() { return storeEmail; }
    public void setStoreEmail(String storeEmail) { this.storeEmail = storeEmail; }

    public String getStorePhone() { return storePhone; }
    public void setStorePhone(String storePhone) { this.storePhone = storePhone; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public String getStoreCity() { return storeCity; }
    public void setStoreCity(String storeCity) { this.storeCity = storeCity; }

    public String getStoreState() { return storeState; }
    public void setStoreState(String storeState) { this.storeState = storeState; }

    public String getStorePostalCode() { return storePostalCode; }
    public void setStorePostalCode(String storePostalCode) { this.storePostalCode = storePostalCode; }

    public String getStoreCountry() { return storeCountry; }
    public void setStoreCountry(String storeCountry) { this.storeCountry = storeCountry; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public boolean isFreeDeliveryAbove() { return freeDeliveryAbove; }
    public void setFreeDeliveryAbove(boolean freeDeliveryAbove) { this.freeDeliveryAbove = freeDeliveryAbove; }

    public double getFreeDeliveryThreshold() { return freeDeliveryThreshold; }
    public void setFreeDeliveryThreshold(double freeDeliveryThreshold) { this.freeDeliveryThreshold = freeDeliveryThreshold; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public boolean isEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(boolean enableNotifications) { this.enableNotifications = enableNotifications; }

    public boolean isEnableEmailNotifications() { return enableEmailNotifications; }
    public void setEnableEmailNotifications(boolean enableEmailNotifications) { this.enableEmailNotifications = enableEmailNotifications; }

    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

    public String getSmtpPort() { return smtpPort; }
    public void setSmtpPort(String smtpPort) { this.smtpPort = smtpPort; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    public boolean isStripeEnabled() { return stripeEnabled; }
    public void setStripeEnabled(boolean stripeEnabled) { this.stripeEnabled = stripeEnabled; }

    public String getStripePublishableKey() { return stripePublishableKey; }
    public void setStripePublishableKey(String stripePublishableKey) { this.stripePublishableKey = stripePublishableKey; }

    public String getStripeSecretKey() { return stripeSecretKey; }
    public void setStripeSecretKey(String stripeSecretKey) { this.stripeSecretKey = stripeSecretKey; }

    public boolean isCodEnabled() { return codEnabled; }
    public void setCodEnabled(boolean codEnabled) { this.codEnabled = codEnabled; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getSupportPhone() { return supportPhone; }
    public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getFaviconUrl() { return faviconUrl; }
    public void setFaviconUrl(String faviconUrl) { this.faviconUrl = faviconUrl; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getAccentColor() { return accentColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getMaintenanceMessage() { return maintenanceMessage; }
    public void setMaintenanceMessage(String maintenanceMessage) { this.maintenanceMessage = maintenanceMessage; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
