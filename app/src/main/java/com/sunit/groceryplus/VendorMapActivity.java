package com.sunit.groceryplus;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.sunit.groceryplus.models.Vendor;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class VendorMapActivity extends AppCompatActivity {

    private MapView map;
    private DatabaseHelper dbHelper;
    private MaterialCardView vendorInfoCard;
    private TextView vendorNameTv, vendorAddressTv, vendorRatingTv;
    private Button visitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_vendor_map);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupMap();
        loadVendors();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.vendorMapToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        map = findViewById(R.id.vendorMap);
        vendorInfoCard = findViewById(R.id.vendorInfoCard);
        vendorNameTv = findViewById(R.id.vendorNameTv);
        vendorAddressTv = findViewById(R.id.vendorAddressTv);
        vendorRatingTv = findViewById(R.id.vendorRatingTv);
        visitBtn = findViewById(R.id.visitVendorBtn);

        visitBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Store: " + vendorNameTv.getText(), Toast.LENGTH_SHORT).show();
            // In a real app, this would navigate to VendorProfileActivity
            finish();
        });
    }

    private void setupMap() {
        map.setMultiTouchControls(true);
        map.getController().setZoom(14.0);
        
        // Center on Kathmandu
        GeoPoint ktmCenter = new GeoPoint(27.7172, 85.3240);
        map.getController().setCenter(ktmCenter);
    }

    private void loadVendors() {
        List<Vendor> vendors = dbHelper.getAllVendors();
        if (vendors.isEmpty()) {
            Toast.makeText(this, "No vendors found nearby", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Vendor vendor : vendors) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(vendor.getLatitude(), vendor.getLongitude()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(vendor.getVendorName());
            
            // Set small vendor icon
            marker.setIcon(getSmallIcon());

            marker.setOnMarkerClickListener((m, mapView) -> {
                showVendorInfo(vendor);
                return true;
            });

            map.getOverlays().add(marker);
        }
        map.invalidate();
    }

    private void showVendorInfo(Vendor vendor) {
        vendorNameTv.setText(vendor.getVendorName());
        vendorAddressTv.setText(vendor.getAddress());
        vendorRatingTv.setText(String.format("Rating: %.1f ⭐", vendor.getRating()));
        vendorInfoCard.setVisibility(View.VISIBLE);
    }

    private Drawable getSmallIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_location);
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, 32, 32);
        drawable.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}
