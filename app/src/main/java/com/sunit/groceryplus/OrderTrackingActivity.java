package com.sunit.groceryplus;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;


/**
 * OrderTrackingActivity - Live tracking interface for active orders.
 * 
 * This activity provides real-time visualization of the order's status and delivery progress.
 * It integrates with OSMDroid to display a map showing the vendor location, delivery destination,
 * and the route path. It also estimates the delivery time based on distance and order status.
 * Crucially, it provides the "Cancel Order" functionality for orders that are still pending.
 * 
 * Key Features:
 * - Interactive Map using OpenStreetMap (OSMDroid)
 * - Vendor and Delivery markers with route line
 * - Live Status updates (Pending, Confirm, Shipped, Delivered, Cancelled)
 * - ETA Calculation based on distance
 * - Order Cancellation (for 'PENDING' orders) with refund policy warning
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class OrderTrackingActivity extends AppCompatActivity {

    private MapView map;
    private int orderId;
    private String orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_order_tracking);

        // Get intent data
        orderId = getIntent().getIntExtra("order_id", -1);
        orderStatus = getIntent().getStringExtra("order_status");

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Track Order #" + orderId);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        TextView statusTv = findViewById(R.id.orderStatusTv);
        if (orderStatus != null) {
            statusTv.setText("Status: " + orderStatus);
        }

        // Setup Map
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        
        // Fetch order and vendor location
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        GeoPoint vendorPoint = new GeoPoint(27.7172, 85.3240); // Default KTM Center
        GeoPoint deliveryPoint = new GeoPoint(27.7000, 85.3000); // Default User Location
        String vendorName = "Warehouse";
        
        com.sunit.groceryplus.models.Order order = dbHelper.getOrderById(orderId);
        if (order != null) {
            // Get delivery address
            com.sunit.groceryplus.models.Address address = dbHelper.getAddressById(order.getAddressId());
            if (address != null && address.getLatitude() != 0) {
                deliveryPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
            }
            
            // Find vendor from order items
            List<com.sunit.groceryplus.models.OrderItem> orderItems = dbHelper.getOrderItems(orderId);
            if (orderItems != null && !orderItems.isEmpty()) {
                com.sunit.groceryplus.models.Product product = dbHelper.getProductById(orderItems.get(0).getProductId());
                if (product != null) {
                    com.sunit.groceryplus.models.Vendor vendor = dbHelper.getVendorById(product.getVendorId());
                    if (vendor != null) {
                        vendorPoint = new GeoPoint(vendor.getLatitude(), vendor.getLongitude());
                        vendorName = vendor.getVendorName();
                    }
                }
            }
        }

        // Center map between vendor and delivery
        double centerLat = (vendorPoint.getLatitude() + deliveryPoint.getLatitude()) / 2;
        double centerLon = (vendorPoint.getLongitude() + deliveryPoint.getLongitude()) / 2;
        map.getController().setCenter(new GeoPoint(centerLat, centerLon));

        // Add Vendor Marker
        Marker vendorMarker = new Marker(map);
        vendorMarker.setPosition(vendorPoint);
        vendorMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        vendorMarker.setTitle(vendorName + " (Vendor)");
        vendorMarker.setSnippet("Product source location");
        map.getOverlays().add(vendorMarker);

        // Add Delivery Marker
        Marker deliveryMarker = new Marker(map);
        deliveryMarker.setPosition(deliveryPoint);
        deliveryMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        deliveryMarker.setTitle("Delivery Location");
        deliveryMarker.setSnippet("Your delivery address");
        map.getOverlays().add(deliveryMarker);

        // Draw Path from Vendor to Delivery
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(vendorPoint);
        geoPoints.add(deliveryPoint);

        Polyline line = new Polyline();
        line.setPoints(geoPoints);
        line.setColor(0xFF4CAF50); // Green for delivery route
        line.setWidth(5f);
        map.getOverlays().add(line);

        // Initialize Cancel Button
        // This button allows users to cancel their order if it is still in PENDING state
        android.widget.Button btnCancel = findViewById(R.id.btnCancelOrder);

        // Check if order status allows cancellation (Only PENDING)
        if ("PENDING".equalsIgnoreCase(orderStatus)) {
            // Make button visible
            btnCancel.setVisibility(android.view.View.VISIBLE);
            
            // Set OnClickListener for cancellation
            btnCancel.setOnClickListener(v -> {
                // Show confirmation dialog to user explaining the 15% deduction policy
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cancel Order?")
                    .setMessage("Are you sure you want to cancel this order?\n\nNOTE: A 15% cancellation fee will be deducted from your refund amount.")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        // User confirmed cancellation
                        // Proceed with cancellation logic in background
                        cancelOrder(orderId);
                    })
                    .setNegativeButton("No", null) // Dismiss dialog on "No"
                    .show();
            });
        } else {
            // Hide button if status is not PENDING (e.g., Processing, Shipped, Delivered)
            btnCancel.setVisibility(android.view.View.GONE);
        }

        // Calculate and display ETA
        calculateAndDisplayEta(vendorPoint, deliveryPoint, orderStatus);
    }

    /**
     * Method to handle order cancellation process.
     * Calls the Repository to update status and calculate refund.
     * 
     * @param orderId The ID of the order to cancel
     */
    private void cancelOrder(int orderId) {
        // Show progress indicator
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Cancelling Order...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Execute DB operation in background thread to avoid UI freezing
        new Thread(() -> {
            // Get current User ID from shared preferences
            int userId = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", -1);
            
            // Call repository to verify and cancel order
            // This method handles:
            // 1. Status update to "Cancelled"
            // 2. Refund calculation (Total - 15%)
            // 3. Chat message to user
            OrderRepository repo = new OrderRepository(this);
            boolean success = repo.cancelOrder(orderId, userId);

            // Update UI on Main Thread
            runOnUiThread(() -> {
                progressDialog.dismiss();
                if (success) {
                    // Show success message
                    android.widget.Toast.makeText(this, "Order Cancelled. Refund initiated.", android.widget.Toast.LENGTH_LONG).show();
                    // Refresh Activity to update status and hide button
                    recreate(); 
                } else {
                    // Show error message
                    android.widget.Toast.makeText(this, "Failed to cancel order. Please try again.", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void calculateAndDisplayEta(GeoPoint p1, GeoPoint p2, String status) {
        TextView etaTv = findViewById(R.id.orderEtaTv);
        if (etaTv == null) return;

        if ("DELIVERED".equalsIgnoreCase(status)) {
            etaTv.setText("Order Delivered");
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(status)) {
            etaTv.setText("Order Cancelled");
            return;
        }

        // Distance in KM
        double distance = calculateDistance(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
        
        // Speed: 0.5 KM per min (30 KM/H)
        int travelTime = (int) Math.ceil(distance / 0.5);
        
        // Processing time based on status
        int processingTime = 0;
        if ("PENDING".equalsIgnoreCase(status)) processingTime = 15;
        else if ("CONFIRMED".equalsIgnoreCase(status)) processingTime = 10;
        else if ("PREPARING".equalsIgnoreCase(status)) processingTime = 5;
        
        int totalMin = travelTime + processingTime;
        
        if (totalMin < 5) {
            etaTv.setText("Arriving in less than 5 mins");
        } else {
            etaTv.setText("Estimated Delivery: " + totalMin + " - " + (totalMin + 5) + " mins");
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth Radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
