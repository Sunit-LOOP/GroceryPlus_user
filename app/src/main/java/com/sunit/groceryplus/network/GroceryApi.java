package com.sunit.groceryplus.network;

import com.sunit.groceryplus.models.Order;
import com.sunit.groceryplus.models.Product;
import com.sunit.groceryplus.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GroceryApi {

    // ================================
    // AUTHENTICATION METHODS
    // ================================

    @POST(ApiConfig.AUTH)
    Call<Map<String, Object>> login(@Body Map<String, String> credentials);

    @POST(ApiConfig.REGISTER)
    Call<Map<String, Object>> register(@Body Map<String, Object> userData);

    // ================================
    // PRODUCTS METHODS
    // ================================

    @GET(ApiConfig.PRODUCTS)
    Call<Map<String, Object>> getProducts(@Query("category") String category,
                                         @Query("search") String search,
                                         @Query("limit") Integer limit,
                                         @Query("offset") Integer offset);

    @GET(ApiConfig.PRODUCTS + "/{productId}")
    Call<Map<String, Object>> getProduct(@Path("productId") int productId);

    // Admin methods for products (keeping existing for compatibility)
    @POST("api/index.php/products")
    Call<Map<String, Object>> createProduct(@Body Map<String, Object> product);

    @PUT("api/index.php/products/{id}")
    Call<Map<String, Object>> updateProduct(@Path("id") int id, @Body Map<String, Object> product);

    @DELETE("api/index.php/products/{id}")
    Call<Map<String, Object>> deleteProduct(@Path("id") int id);

    // ================================
    // CATEGORIES METHODS
    // ================================

    @GET(ApiConfig.CATEGORIES)
    Call<List<Map<String, Object>>> getCategories();

    // ================================
    // CART METHODS
    // ================================

    @GET(ApiConfig.CART)
    Call<Map<String, Object>> getCart();

    @POST(ApiConfig.CART)
    Call<Map<String, Object>> addToCart(@Body Map<String, Object> cartData);

    @PUT(ApiConfig.CART + "/{cartItemId}")
    Call<Map<String, Object>> updateCartItem(@Path("cartItemId") int cartItemId, @Body Map<String, Object> updateData);

    @DELETE(ApiConfig.CART + "/{cartItemId}")
    Call<Map<String, Object>> removeFromCart(@Path("cartItemId") int cartItemId);

    // ================================
    // ORDERS METHODS
    // ================================

    @GET(ApiConfig.ORDERS)
    Call<List<Map<String, Object>>> getOrders();

    @POST(ApiConfig.ORDERS)
    Call<Map<String, Object>> placeOrder(@Body Map<String, Object> orderData);

    // Admin methods for orders (keeping existing for compatibility)
    @GET("api/index.php/orders/{id}")
    Call<Map<String, Object>> getOrder(@Path("id") int id);

    @PUT("api/index.php/orders/{id}")
    Call<Map<String, Object>> updateOrder(@Path("id") int id, @Body Map<String, Object> updateData);

    // ================================
    // FAVORITES METHODS
    // ================================

    @GET(ApiConfig.FAVORITES)
    Call<List<Map<String, Object>>> getFavorites();

    @POST(ApiConfig.FAVORITES)
    Call<Map<String, Object>> addToFavorites(@Body Map<String, Object> favData);

    @DELETE(ApiConfig.FAVORITES + "/{productId}")
    Call<Map<String, Object>> removeFromFavorites(@Path("productId") int productId);

    // ================================
    // REVIEWS METHODS
    // ================================

    @GET(ApiConfig.REVIEWS + "/{productId}")
    Call<List<Map<String, Object>>> getProductReviews(@Path("productId") int productId);

    @POST(ApiConfig.REVIEWS)
    Call<Map<String, Object>> submitReview(@Body Map<String, Object> reviewData);

    // ================================
    // MESSAGES METHODS
    // ================================

    @GET(ApiConfig.MESSAGES)
    Call<List<Map<String, Object>>> getConversations();

    @GET(ApiConfig.MESSAGES + "/{userId}")
    Call<List<Map<String, Object>>> getMessagesWithUser(@Path("userId") int userId);

    @POST(ApiConfig.MESSAGES)
    Call<Map<String, Object>> sendMessage(@Body Map<String, Object> messageData);

    // Keeping old message endpoints for compatibility
    @GET("api/index.php/messages")
    Call<List<Map<String, Object>>> getMessages();

    @GET("api/index.php/messages/{userId}")
    Call<List<Map<String, Object>>> getMessagesForUser(@Path("userId") int userId);

    @POST("api/index.php/messages")
    Call<Map<String, Object>> sendMessageOld(@Body Map<String, Object> messageData);

    // ================================
    // ANALYTICS METHODS
    // ================================

    @GET(ApiConfig.ANALYTICS)
    Call<Map<String, Object>> getAnalytics();

    // ================================
    // NOTIFICATIONS METHODS
    // ================================

    @GET(ApiConfig.NOTIFICATIONS)
    Call<List<Map<String, Object>>> getNotifications();

    // ================================
    // LEGACY USER METHODS (for admin)
    // ================================

    @GET("api/index.php/users")
    Call<List<User>> getUsers();

    @GET("api/index.php/users/{id}")
    Call<User> getUser(@Path("id") int id);

    @POST("api/index.php/users")
    Call<Map<String, Object>> createUser(@Body User user);

    @PUT("api/index.php/users/{id}")
    Call<Map<String, Object>> updateUser(@Path("id") int id, @Body User user);

    @DELETE("api/index.php/users/{id}")
    Call<Map<String, Object>> deleteUser(@Path("id") int id);

    // Legacy auth (keeping for compatibility)
    @POST("api/auth.php")
    Call<Map<String, Object>> authenticate(@Body Map<String, String> credentials);
}