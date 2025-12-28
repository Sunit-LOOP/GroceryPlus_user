package com.sunit.groceryplus.network;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthManager {
    private static final String PREF_TOKEN = "auth_token";
    private static final String PREF_USER_DATA = "user_data";

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences("GroceryPlus", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GroceryPlus", Context.MODE_PRIVATE);
        return prefs.getString(PREF_TOKEN, null);
    }

    public static void saveUserData(Context context, JSONObject userData) {
        SharedPreferences prefs = context.getSharedPreferences("GroceryPlus", Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_USER_DATA, userData.toString()).apply();
    }

    public static JSONObject getUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GroceryPlus", Context.MODE_PRIVATE);
        String userDataStr = prefs.getString(PREF_USER_DATA, null);
        if (userDataStr != null) {
            try {
                return new JSONObject(userDataStr);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GroceryPlus", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}