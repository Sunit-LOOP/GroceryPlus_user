package com.sunit.groceryplus.network;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

/** Manual API service provider for fetching product and category data (currently implemented as stubs). */
public class ApiService {

    private Context context;

    /** Initializes the API service with the provided application context. */
    public ApiService(Context context) {
        this.context = context;
    }

    /** Generic callback interface for handling asynchronous network responses. */
    public interface ApiCallback<T> {
        /** Called upon successful completion of the network request. */
        void onSuccess(T response);
        /** Called when the network request results in an error. */
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