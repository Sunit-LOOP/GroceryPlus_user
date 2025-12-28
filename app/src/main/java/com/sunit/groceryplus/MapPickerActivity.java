package com.sunit.groceryplus;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MapPickerActivity extends AppCompatActivity {

    private MapView map;
    private TextView locationTv;
    private Button confirmBtn;
    private GeoPoint selectedPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map_picker);

        Toolbar toolbar = findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        map = findViewById(R.id.mapPicker);
        locationTv = findViewById(R.id.selectedLocationTv);
        confirmBtn = findViewById(R.id.confirmLocationBtn);

        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        
        // Default center: Kathmandu, Nepal
        GeoPoint startPoint = new GeoPoint(27.7172, 85.3240);
        map.getController().setCenter(startPoint);

        map.addMapListener(new org.osmdroid.events.MapListener() {
            @Override
            public boolean onScroll(org.osmdroid.events.ScrollEvent event) {
                updateSelectedLocation();
                return true;
            }

            @Override
            public boolean onZoom(org.osmdroid.events.ZoomEvent event) {
                updateSelectedLocation();
                return true;
            }
        });

        confirmBtn.setOnClickListener(v -> {
            if (selectedPoint != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat", selectedPoint.getLatitude());
                resultIntent.putExtra("lng", selectedPoint.getLongitude());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        updateSelectedLocation();
    }

    private void updateSelectedLocation() {
        selectedPoint = (GeoPoint) map.getMapCenter();
        locationTv.setText(String.format("Lat: %.6f, Lng: %.6f", selectedPoint.getLatitude(), selectedPoint.getLongitude()));
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
