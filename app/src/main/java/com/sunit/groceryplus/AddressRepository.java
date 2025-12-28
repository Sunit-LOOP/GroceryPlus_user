package com.sunit.groceryplus;

import android.content.Context;
import android.util.Log;

import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.models.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressRepository {
    private static final String TAG = "AddressRepository";
    private DatabaseHelper dbHelper;

    public AddressRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long addAddress(int userId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        try {
            return dbHelper.addAddress(userId, type, fullAddress, landmark, city, area, latitude, longitude, isDefault);
        } catch (Exception e) {
            Log.e(TAG, "Error adding address", e);
            return -1;
        }
    }

    public List<Address> getUserAddresses(int userId) {
        try {
            return dbHelper.getUserAddresses(userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user addresses", e);
            return new ArrayList<>();
        }
    }

    public boolean deleteAddress(int addressId) {
        return dbHelper.deleteAddress(addressId);
    }

    public boolean setDefaultAddress(int userId, int addressId) {
        return dbHelper.setDefaultAddress(userId, addressId);
    }

    public boolean updateAddress(int addressId, String type, String fullAddress, String landmark, String city, String area, double latitude, double longitude, boolean isDefault) {
        try {
            return dbHelper.updateAddress(addressId, type, fullAddress, landmark, city, area, latitude, longitude, isDefault);
        } catch (Exception e) {
            Log.e(TAG, "Error updating address", e);
            return false;
        }
    }
}
