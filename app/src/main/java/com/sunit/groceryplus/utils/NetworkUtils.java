package com.sunit.groceryplus.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * NetworkUtils - Utility class for checking network connectivity status.
 * 
 * This class provides comprehensive network connectivity checking functionality
 * for the GroceryPlus app. It supports different Android API levels and
 * provides methods to check various types of network connections.
 * 
 * Key Features:
 * - Internet connectivity checking
 * - WiFi connection detection
 * - Mobile data connection detection
 * - API level compatibility (supports both legacy and modern APIs)
 * - Network type identification
 */
public class NetworkUtils {
    
    /**
     * Check if device has active internet connection.
     * 
     * This method determines if the device has any active internet connection
     * regardless of the type (WiFi, mobile data, or ethernet). It uses different
     * approaches based on the Android API level for compatibility.
     * 
     * For Android M (API 23) and above: Uses NetworkCapabilities API
     * For older versions: Uses deprecated NetworkInfo API
     * 
     * @param context The application context for accessing system services
     * @return true if device has active internet connection, false otherwise
     */
    public static boolean isOnline(Context context) {
        // Get the system connectivity service
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // If connectivity manager is not available, return false
        if (connectivityManager == null) {
            return false;
        }
        
        // Use modern NetworkCapabilities API for Android M and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the active network
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            // Check network capabilities for different transport types
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && 
                   (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            // For devices below Android M, use deprecated NetworkInfo API
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
    
    /**
     * Check if device is connected to WiFi network.
     * 
     * This method specifically checks if the device has an active WiFi connection.
     * It uses different APIs based on the Android version for compatibility.
     * 
     * @param context The application context for accessing system services
     * @return true if device is connected to WiFi, false otherwise
     */
    public static boolean isWifiConnected(Context context) {
        // Get the system connectivity service
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // If connectivity manager is not available, return false
        if (connectivityManager == null) {
            return false;
        }
        
        // Use modern NetworkCapabilities API for Android M and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the active network
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            // Check if network has WiFi transport capability
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            // For devices below Android M, use deprecated NetworkInfo API
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return networkInfo != null && networkInfo.isConnected();
        }
    }
    
    /**
     * Check if device is connected to mobile data network.
     * 
     * This method specifically checks if the device has an active mobile data
     * connection (cellular network). It uses different APIs based on the
     * Android version for compatibility.
     * 
     * @param context The application context for accessing system services
     * @return true if device is connected to mobile data, false otherwise
     */
    public static boolean isMobileConnected(Context context) {
        // Get the system connectivity service
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // If connectivity manager is not available, return false
        if (connectivityManager == null) {
            return false;
        }
        
        // Use modern NetworkCapabilities API for Android M and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the active network
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            
            // Check if network has cellular transport capability
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && 
                   capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        } else {
            // For devices below Android M, use deprecated NetworkInfo API
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return networkInfo != null && networkInfo.isConnected();
        }
    }
    
    /**
     * Get descriptive string of current network type.
     * 
     * This method returns a human-readable string describing the current
     * network connection type. It's useful for debugging, logging, and
     * displaying network status to users.
     * 
     * @param context The application context for network checking
     * @return String representing network type: "Offline", "WiFi", "Mobile", or "Other"
     */
    public static String getNetworkType(Context context) {
        // First check if device is online
        if (!isOnline(context)) {
            return "Offline";
        }
        
        // Check specific connection types
        if (isWifiConnected(context)) {
            return "WiFi";
        } else if (isMobileConnected(context)) {
            return "Mobile";
        } else {
            // Other types like ethernet, VPN, etc.
            return "Other";
        }
    }
}
