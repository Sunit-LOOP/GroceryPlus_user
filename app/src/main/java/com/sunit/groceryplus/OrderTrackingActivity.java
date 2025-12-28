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
