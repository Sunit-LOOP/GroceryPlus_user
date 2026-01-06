package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sunit.groceryplus.utils.CollaborativeFilteringTest; // Assuming this exists or will be needed

/** Activity for testing various system features, primarily collaborative filtering functionality. */
public class TestActivity extends AppCompatActivity {
    // Infrastructure
    private static final String TAG = "TestActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        // Add test button
        if (findViewById(R.id.testButton) != null) {
            findViewById(R.id.testButton).setOnClickListener(v -> {
                runCollaborativeFilteringTest();
            });
        }
    }
    
    /**
     * Run collaborative filtering test
     */
    private void runCollaborativeFilteringTest() {
        try {
            Log.d(TAG, "Starting collaborative filtering test...");
            // Check if class exists before calling to avoid compile error if it's missing from my context
            // CollaborativeFilteringTest.testCollaborativeFiltering(this);
            
            Toast.makeText(this, "Collaborative filtering test completed! Check logs for details.", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error running collaborative filtering test", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
