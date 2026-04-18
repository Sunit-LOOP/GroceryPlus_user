package com.sunit.groceryplus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.graphics.DashPathEffect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.sunit.groceryplus.R;
import com.sunit.groceryplus.DatabaseHelper;
import com.sunit.groceryplus.models.DeliveryPersonnel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/** OrderTrackingActivity - Real-time order visualization featuring OSMDroid map integration and ETA calculations. */
public class OrderTrackingActivity extends AppCompatActivity {

    private static final String TAG = "OrderTrackingActivity";

    // Infrastructure & UI
    private MapView map;
    private int orderId;
    private String orderStatus;
    
    // Real-time tracking
    private Marker deliveryPersonMarker;
    private Timer locationUpdateTimer;
    private Handler uiHandler;
    private DatabaseHelper dbHelper;
    private GeoPoint vendorPoint, deliveryPoint;
    private String vendorName;
    
    // Enhanced polylines for route visualization
    private Polyline vendorToDeliveryRoute;
    private Polyline deliveryPersonToCustomerRoute;
    private Polyline completedRouteSegment;
    private List<Polyline> allRoutePolylines;

    /** Initializes the map activity, configures OSMDroid, and plots delivery route. */
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

        // Setup Map
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        
        // Initialize real-time tracking components
        uiHandler = new Handler(Looper.getMainLooper());
        dbHelper = new DatabaseHelper(this);
        allRoutePolylines = new ArrayList<>();
        
        // Fetch order and vendor location
        vendorPoint = new GeoPoint(27.7172, 85.3240); // Default KTM Center
        deliveryPoint = new GeoPoint(27.7000, 85.3000); // Default User Location
        vendorName = "Warehouse";
        
        com.sunit.groceryplus.models.Order order = dbHelper.getOrderById(orderId);
        // Prefer live DB status — many entry points (notifications, etc.) omit order_status from the Intent.
        if (order != null) {
            String st = order.getStatus();
            orderStatus = (st != null) ? st.trim() : null;
        }
        if (orderStatus != null) {
            statusTv.setText("Status: " + orderStatus);
        }

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

        // Draw Path from Vendor to Delivery with enhanced polylines
        createEnhancedRoute(vendorPoint, deliveryPoint);
        
        // Add Delivery Personnel Marker for real-time tracking
        addDeliveryPersonnelMarker();
        
        // Start real-time location updates
        startRealTimeTracking();

        // Initialize Cancel Button (shown only while order is still Pending or Processing)
        android.widget.Button btnCancel = findViewById(R.id.btnCancelOrder);

        // Match OrderHistory / OrderAdapter: cancellable while Pending or Processing
        if (orderStatus != null
                && ("pending".equalsIgnoreCase(orderStatus) || "processing".equalsIgnoreCase(orderStatus))) {
            // Make button visible
            btnCancel.setVisibility(android.view.View.VISIBLE);
            
            // Set OnClickListener for cancellation
            btnCancel.setOnClickListener(v ->
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cancel order?")
                    .setMessage("Paid with card: refund (after 15% fee) is added to your wallet right away.")
                    .setPositiveButton("Cancel order", (dialog, which) -> cancelOrder(orderId))
                    .setNegativeButton("Keep order", null)
                    .show());
        } else {
            // Hide button if order is not in a user-cancellable state
            btnCancel.setVisibility(android.view.View.GONE);
        }

        // Calculate and display ETA
        calculateAndDisplayEta(vendorPoint, deliveryPoint, orderStatus);
    }

    /** Processes order cancellation with a 15% fee deduction policy. */
    private void cancelOrder(int orderId) {
        // Show progress indicator
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Cancelling Order...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Execute DB operation in background thread to avoid UI freezing
        new Thread(() -> {
            // Get current User ID from shared preferences using correct file and key
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);
            
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
                    double bal = dbHelper.getWalletBalance(userId);
                    android.widget.Toast.makeText(this,
                            "Order #" + orderId + " cancelled. Wallet: NPR " + String.format(java.util.Locale.getDefault(), "%.2f", bal),
                            android.widget.Toast.LENGTH_LONG).show();
                    finish(); // Return to previous screen
                } else {
                    // Show error message natively catching last repo trace
                    String errorMsg = "Failed to cancel order: ";
                    if (com.sunit.groceryplus.OrderRepository.lastError != null && !com.sunit.groceryplus.OrderRepository.lastError.isEmpty()) {
                        errorMsg += com.sunit.groceryplus.OrderRepository.lastError;
                    }
                    android.widget.Toast.makeText(this, errorMsg, android.widget.Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    /** Updates the ETA display based on geographical distance and order status. */
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
        else if ("PROCESSING".equalsIgnoreCase(status)) processingTime = 10;
        else if ("CONFIRMED".equalsIgnoreCase(status)) processingTime = 10;
        else if ("PREPARING".equalsIgnoreCase(status)) processingTime = 5;
        
        int totalMin = travelTime + processingTime;
        
        if (totalMin < 5) {
            etaTv.setText("Arriving in less than 5 mins");
        } else {
            etaTv.setText("Estimated Delivery: " + totalMin + " - " + (totalMin + 5) + " mins");
        }
    }

    /** Haversine formula to calculate distance between two coordinates in Kilometers. */
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

    /** Handles toolbar menu selections. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Adds delivery personnel marker to the map. */
    private void addDeliveryPersonnelMarker() {
        // Get delivery personnel assigned to this order
        com.sunit.groceryplus.models.Order order = dbHelper.getOrderById(orderId);
        if (order != null && order.getDeliveryPersonId() > 0) {
            android.database.Cursor cursor = dbHelper.getDeliveryPersonnelById(order.getDeliveryPersonId());
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    com.sunit.groceryplus.models.DeliveryPersonnel deliveryPerson = new com.sunit.groceryplus.models.DeliveryPersonnel();
                    deliveryPerson.setDeliveryPersonId(cursor.getInt(cursor.getColumnIndexOrThrow("person_id")));
                    deliveryPerson.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                    deliveryPerson.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    deliveryPerson.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                    deliveryPerson.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                    deliveryPerson.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("available")) == 1);
                    
                    deliveryPersonMarker = new Marker(map);
                    deliveryPersonMarker.setPosition(new GeoPoint(deliveryPerson.getLatitude(), deliveryPerson.getLongitude()));
                    deliveryPersonMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    deliveryPersonMarker.setTitle(deliveryPerson.getName() + " (Delivery Partner)");
                    deliveryPersonMarker.setSnippet("Your delivery partner");
                    deliveryPersonMarker.setIcon(getResources().getDrawable(R.drawable.ic_delivery_person));
                    map.getOverlays().add(deliveryPersonMarker);
                }
                cursor.close();
            }
        }
    }
    
    /** Starts real-time location updates for delivery personnel. */
    private void startRealTimeTracking() {
        if (locationUpdateTimer != null) {
            locationUpdateTimer.cancel();
        }
        
        locationUpdateTimer = new Timer();
        locationUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDeliveryPersonnelLocation();
            }
        }, 0, 5000); // Update every 5 seconds
    }
    
    /** Updates delivery personnel location from database with enhanced route visualization. */
    private void updateDeliveryPersonnelLocation() {
        try {
            com.sunit.groceryplus.models.Order order = dbHelper.getOrderById(orderId);
            if (order != null && order.getDeliveryPersonId() > 0 && 
                !"DELIVERED".equalsIgnoreCase(order.getStatus()) && 
                !"CANCELLED".equalsIgnoreCase(order.getStatus())) {
                
                android.database.Cursor cursor = dbHelper.getDeliveryPersonnelById(order.getDeliveryPersonId());
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        com.sunit.groceryplus.models.DeliveryPersonnel deliveryPerson = new com.sunit.groceryplus.models.DeliveryPersonnel();
                        deliveryPerson.setDeliveryPersonId(cursor.getInt(cursor.getColumnIndexOrThrow("person_id")));
                        deliveryPerson.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                        deliveryPerson.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                        deliveryPerson.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                        deliveryPerson.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                        deliveryPerson.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow("available")) == 1);
                        
                        if (deliveryPersonMarker != null) {
                            GeoPoint newLocation = new GeoPoint(deliveryPerson.getLatitude(), deliveryPerson.getLongitude());
                        
                            uiHandler.post(() -> {
                                deliveryPersonMarker.setPosition(newLocation);
                                
                                // Update route from delivery person to customer with enhanced polylines
                                updateDeliveryRoute(newLocation, deliveryPoint);
                                
                                // Update route progress based on delivery status
                                updateRouteProgress(order.getStatus(), newLocation);
                                
                                // Update ETA based on current location
                                calculateAndDisplayEta(newLocation, deliveryPoint, order.getStatus());
                            });
                        }
                    }
                    cursor.close();
                }
                cursor.close();
            }
        } catch (Exception e) {
            // Log error but don't crash
            Log.e(TAG, "Error updating delivery personnel location", e);
        }
    }
    
    /** Updates route progress visualization based on delivery status. */
    private void updateRouteProgress(String status, GeoPoint currentLocation) {
        // Clear completed route segments
        if (completedRouteSegment != null) {
            map.getOverlays().remove(completedRouteSegment);
            allRoutePolylines.remove(completedRouteSegment);
        }
        
        if ("SHIPPED".equalsIgnoreCase(status) || "OUT_FOR_DELIVERY".equalsIgnoreCase(status)) {
            // Create completed route segment from vendor to current location
            List<GeoPoint> completedPoints = new ArrayList<>();
            completedPoints.add(vendorPoint);
            completedPoints.add(currentLocation);
            completedRouteSegment = createStyledPolyline(completedPoints, 0xFF4CAF50, 8f, false);
            allRoutePolylines.add(completedRouteSegment);
            map.getOverlays().add(completedRouteSegment);
        }
        
        map.invalidate();
    }
    
    /** Creates enhanced multi-segment route with intermediate waypoints. */
    private void createEnhancedRoute(GeoPoint start, GeoPoint end) {
        // Clear existing polylines
        if (allRoutePolylines != null) {
            for (Polyline polyline : allRoutePolylines) {
                map.getOverlays().remove(polyline);
            }
            allRoutePolylines.clear();
        }
        
        // Create simple route with start and end points
        List<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.add(end);
        
        // Create main route polyline (vendor to delivery)
        vendorToDeliveryRoute = createStyledPolyline(waypoints, 0xFF4CAF50, 6f, true);
        allRoutePolylines.add(vendorToDeliveryRoute);
        map.getOverlays().add(vendorToDeliveryRoute);
        
        // Create route outline for better visibility
        Polyline routeOutline = createStyledPolyline(waypoints, 0x804CAF50, 8f, false);
        allRoutePolylines.add(routeOutline);
        map.getOverlays().add(routeOutline);
    }
    
    /** Updates delivery route from delivery person to customer with enhanced polylines. */
    private void updateDeliveryRoute(GeoPoint from, GeoPoint to) {
        // Clear existing delivery person route
        if (deliveryPersonToCustomerRoute != null) {
            map.getOverlays().remove(deliveryPersonToCustomerRoute);
            allRoutePolylines.remove(deliveryPersonToCustomerRoute);
        }
        
        // Create new route from delivery person to customer
        List<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(from);
        routePoints.add(to);
        deliveryPersonToCustomerRoute = createStyledPolyline(routePoints, 0xFF2196F3, 4f, true);
        allRoutePolylines.add(deliveryPersonToCustomerRoute);
        map.getOverlays().add(deliveryPersonToCustomerRoute);
        
        // Add animated pulse effect for active route
        Polyline pulseRoute = createStyledPolyline(routePoints, 0x80FF9800, 6f, false);
        allRoutePolylines.add(pulseRoute);
        map.getOverlays().add(pulseRoute);
        
        map.invalidate();
    }
    
    /** Creates a styled polyline with specified color, width, and pattern. */
    private Polyline createStyledPolyline(List<GeoPoint> points, int color, float width, boolean isDashed) {
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.setColor(color);
        polyline.setWidth(width);
        
        if (isDashed) {
            // Create dashed line effect
            polyline.getPaint().setPathEffect(new android.graphics.DashPathEffect(new float[]{20f, 10f}, 0f));
        }
        
        return polyline;
    }
    
    /** Stop real-time location tracking. */
    private void stopRealTimeTracking() {
        if (locationUpdateTimer != null) {
            locationUpdateTimer.cancel();
            locationUpdateTimer = null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRealTimeTracking();
    }
}
