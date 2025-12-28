package com.sunit.groceryplus.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiService {
    private static final String TAG = "ApiService";
    private Context context;
    private GroceryApi api;

    public ApiService(Context context) {
        this.context = context;
        this.api = ApiClient.getGroceryApi();
    }

    private JSONObject mapToJson(Map<String, Object> map) throws JSONException {
        return new JSONObject(map);
    }

    // ================================
    // AUTHENTICATION METHODS
    // ================================

    public void login(String email, String password, ApiCallback<JSONObject> callback) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        api.login(credentials).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> responseBody = response.body();
                        String token = (String) responseBody.get("token");
                        @SuppressWarnings("unchecked")
                        Map<String, Object> user = (Map<String, Object>) responseBody.get("user");

                        if (token != null && user != null) {
                            AuthManager.saveToken(context, token);
                            AuthManager.saveUserData(context, new JSONObject(user));
                        }

                        callback.onSuccess(mapToJson(responseBody));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void register(String name, String email, String password, String phone, ApiCallback<JSONObject> callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("phone", phone);

        api.register(userData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    // ================================
    // PRODUCTS METHODS
    // ================================

    public void getProducts(String category, String search, Integer limit, Integer offset, ApiCallback<JSONObject> callback) {
        api.getProducts(category, search, limit, offset).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    // ================================
    // CART METHODS
    // ================================

    public void getCart(ApiCallback<JSONObject> callback) {
        api.getCart().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load cart: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void addToCart(int productId, int quantity, ApiCallback<JSONObject> callback) {
        Map<String, Object> cartData = new HashMap<>();
        cartData.put("product_id", productId);
        cartData.put("quantity", quantity);

        api.addToCart(cartData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to add to cart: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void updateCartItem(int cartItemId, Map<String, Object> updateData, ApiCallback<JSONObject> callback) {
        api.updateCartItem(cartItemId, updateData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to update cart item: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void removeFromCart(int cartItemId, ApiCallback<JSONObject> callback) {
        api.removeFromCart(cartItemId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to remove from cart: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getCategories(ApiCallback<JSONArray> callback) {
        api.getCategories().enqueue(new Callback<java.util.List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<java.util.List<Map<String, Object>>> call, Response<java.util.List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (Map<String, Object> item : response.body()) {
                            jsonArray.put(mapToJson(item));
                        }
                        callback.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load categories: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<Map<String, Object>>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getProductReviews(int productId, ApiCallback<java.util.List<JSONObject>> callback) {
        api.getProductReviews(productId).enqueue(new Callback<java.util.List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<java.util.List<Map<String, Object>>> call, Response<java.util.List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        java.util.List<JSONObject> jsonList = new java.util.ArrayList<>();
                        for (Map<String, Object> item : response.body()) {
                            jsonList.add(mapToJson(item));
                        }
                        callback.onSuccess(jsonList);
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load reviews: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<Map<String, Object>>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void submitReview(Map<String, Object> reviewData, ApiCallback<JSONObject> callback) {
        api.submitReview(reviewData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to submit review: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    // ================================
    // ORDERS METHODS
    // ================================

    public void getOrders(ApiCallback<JSONArray> callback) {
        api.getOrders().enqueue(new Callback<java.util.List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<java.util.List<Map<String, Object>>> call, Response<java.util.List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (Map<String, Object> item : response.body()) {
                            jsonArray.put(mapToJson(item));
                        }
                        callback.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load orders: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<Map<String, Object>>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void placeOrder(JSONArray items, String deliveryAddress, ApiCallback<JSONObject> callback) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("cart_items", items);
        orderData.put("delivery_address", deliveryAddress != null ? deliveryAddress : "Default Address");

        api.placeOrder(orderData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to place order: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    // ================================
    // FAVORITES METHODS
    // ================================

    public void getFavorites(ApiCallback<JSONArray> callback) {
        api.getFavorites().enqueue(new Callback<java.util.List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<java.util.List<Map<String, Object>>> call, Response<java.util.List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (Map<String, Object> item : response.body()) {
                            jsonArray.put(mapToJson(item));
                        }
                        callback.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to load favorites: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<java.util.List<Map<String, Object>>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void addToFavorites(int productId, ApiCallback<JSONObject> callback) {
        Map<String, Object> favData = new HashMap<>();
        favData.put("product_id", productId);

        api.addToFavorites(favData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(mapToJson(response.body()));
                    } catch (JSONException e) {
                        callback.onError("Parse Error: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to add to favorites: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                callback.onError("Network Error: " + t.getMessage());
            }
        });
    }

    // Generic callback interface
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}