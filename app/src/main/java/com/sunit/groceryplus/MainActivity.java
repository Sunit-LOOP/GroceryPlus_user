package com.sunit.groceryplus;

import android.os.Bundle;
import android.util.Log;
import com.sunit.groceryplus.models.User;
import com.sunit.groceryplus.network.ApiClient;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity - Entry point and initialization activity for GroceryPlus
 * 
 * This activity serves as the main entry point for the GroceryPlus application.
 * It handles initial setup, database testing, and API client initialization.
 * The activity uses EdgeToEdge display for a modern, full-screen experience.
 * 
 * Key Responsibilities:
 * - Initialize EdgeToEdge display for modern UI
 * - Setup API client with application context
 * - Test database connectivity and default user authentication
 * - Verify system readiness before launching main features
 * 
 * Testing Features:
 * - Default admin account verification
 * - Database connection testing
 * - User existence validation
 * - Error logging and debugging support
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {
    
    // Tag for logging and debugging
    private static final String TAG = "MainActivity";

    /**
     * Called when the activity is first created
     * 
     * This method initializes the main activity with EdgeToEdge display,
     * sets up the API client, and performs database testing to ensure
     * the application is ready for use.
     * 
     * @param savedInstanceState Previously saved state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable EdgeToEdge display for modern full-screen experience
        EdgeToEdge.enable(this);
        
        // Set the main layout content
        setContentView(R.layout.activity_main);
        
        // Apply window insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API client with application context for network operations
        ApiClient.setContext(this);

        // Perform database testing to verify system readiness
        testDatabase();
    }

    /**
     * Test database connectivity and verify default user accounts
     * 
     * This method performs essential database tests to ensure the application
     * is properly configured and ready for use. It tests authentication
     * with the default admin account and verifies user data integrity.
     * 
     * Tests Performed:
     * 1. Database helper initialization
     * 2. Default admin authentication
     * 3. Sample user existence verification
     * 4. Error handling and logging
     */
    private void testDatabase() {
        try {
            // Initialize database helper for testing
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            
            // Test authentication with default admin account
            // This verifies that the default admin user is properly created
            // and can authenticate with the correct credentials
            User adminUser = dbHelper.authenticateUser("admin@gmail.com", "admin123");
            if (adminUser != null) {
                Log.d(TAG, "Default admin authenticated: " + adminUser.getName() + " (" + adminUser.getUserType() + ")");
            } else {
                Log.e(TAG, "Failed to authenticate default admin");
            }
            
            // Test user existence check for sample user account
            // This verifies that user data is properly stored and retrievable
            boolean userExists = dbHelper.isUserExists("ram@gmail.com");
            Log.d(TAG, "User 'ram@gmail.com' exists: " + userExists);
            
        } catch (Exception e) {
            // Log any database errors for debugging purposes
            Log.e(TAG, "Database test failed", e);
        }
    }
}