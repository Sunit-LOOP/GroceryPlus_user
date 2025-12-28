package com.sunit.groceryplus.network;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiService {

    private Context context;

    public ApiService(Context context) {
        this.context = context;
    }

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }

    public void getCategories(ApiCallback<JSONArray> callback) {
        // This is a stub implementation.
        // In a real application, you would make a network request here.
        callback.onError("Not implemented");
    }

    public void getProducts(String category, String searchTerm, Integer limit, Integer offset, ApiCallback<JSONObject> callback) {
        // This is a stub implementation.
        // In a real application, you would make a network request here.
        callback.onError("Not implemented");
    }
}