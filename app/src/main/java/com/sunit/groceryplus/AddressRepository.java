package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.Address;

import java.util.ArrayList;
import java.util.List;

/** Repository for managing user delivery addresses in the database. */
public class AddressRepository {
    // Infrastructure
    private static final String TAG = "AddressRepository";
    private DatabaseHelper dbHelper;

    /** Initializes the repository with a DatabaseHelper. */
    public AddressRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Adds a new address to the database. */
    public long addAddress(int userId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        try {
            return dbHelper.addAddress(userId, type, fullAddress, landmark, city, area, latitude, longitude, isDefault);
        } catch (Exception e) {
            Log.e(TAG, "Error adding address", e);
            return -1;
        }
    }

    /** Retrieves all addresses for a specific user. */
    public List<Address> getUserAddresses(int userId) {
        try {
            return dbHelper.getUserAddresses(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user addresses", e);
            return new ArrayList<>();
        }
    }

    /** Deletes an address by its ID. */
    public boolean deleteAddress(int addressId) {
        return dbHelper.deleteAddress(addressId);
    }

    /** Sets an address as the default for a user. */
    public boolean setDefaultAddress(int userId, int addressId) {
        return dbHelper.setDefaultAddress(userId, addressId);
    }

    /** Updates an existing address in the database. */
    public boolean updateAddress(int addressId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        try {
            return dbHelper.updateAddress(addressId, type, fullAddress, landmark, city, area, latitude, longitude, isDefault);
        } catch (Exception e) {
            Log.e(TAG, "Error updating address", e);
            return false;
        }
    }
}
