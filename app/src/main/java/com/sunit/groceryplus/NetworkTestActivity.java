package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTestActivity extends AppCompatActivity {
    private static final String TAG = "NetworkTest";
    private TextView statusTextView;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment); // Reusing existing layout for testing
        
        statusTextView = findViewById(R.id.paymentTotalAmount);
        testButton = findViewById(R.id.paymentPayNowBtn);
        
        testButton.setText("Test Network Connection");
        
        testButton.setOnClickListener(v -> testNetworkConnection());
    }
    
    private void testNetworkConnection() {
        new Thread(() -> {
            try {
                // Test connection to the backend server
                URL url = new URL("http://10.0.2.2:4567/create-payment-intent");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000); // 5 seconds timeout
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Content-Type", "application/json");
                
                int responseCode = connection.getResponseCode();
                
                runOnUiThread(() -> {
                    if (responseCode == 405) {
                        // 405 Method Not Allowed is expected since we didn't send POST data
                        statusTextView.setText("Network OK: Connected to backend server (HTTP " + responseCode + ")");
                        Toast.makeText(this, "Network connection successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        statusTextView.setText("Network response: HTTP " + responseCode);
                        Toast.makeText(this, "Server responded with HTTP " + responseCode, Toast.LENGTH_SHORT).show();
                    }
                });
                
                connection.disconnect();
            } catch (IOException e) {
                Log.e(TAG, "Network test failed", e);
                runOnUiThread(() -> {
                    statusTextView.setText("Network Error: " + e.getMessage());
                    Toast.makeText(this, "Network test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}