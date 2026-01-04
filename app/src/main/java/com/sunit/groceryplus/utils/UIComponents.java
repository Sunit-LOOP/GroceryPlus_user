package com.sunit.groceryplus.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sunit.groceryplus.R;

/**
 * UIComponents - Utility class for creating modern UI components.
 * 
 * This class provides static methods for creating consistent, modern UI components
 * throughout the GroceryPlus application. It implements Material Design 3 principles
 * and provides a unified visual language across all screens.
 * 
 * Key Features:
 * - Modern card styling with elevation and ripple effects
 * - Gradient buttons with custom colors
 * - Material Design FAB styling
 * - Status badges with color coding
 * - Chip-style buttons for selections
 * - Shimmer effects for loading states
 * - Price tags and discount badges
 * - Rating displays with visual feedback
 * - Stock indicators with dynamic coloring
 * - Delivery status indicators
 * - Category chips with Material Design
 * - Gradient backgrounds and borders
 * 
 * Color Scheme:
 * - Primary: #6750A4 (Material Blue)
 * - Success: #4CAF50 (Material Green)
 * - Warning: #FF9800 (Material Orange)
 * - Error: #F44336 (Material Red)
 * - Info: #2196F3 (Material Light Blue)
 * - Surface: #F5F5F5 (Material Gray)
 * 
 * Design Patterns:
 * - Material Design 3 compliance
 * - Consistent corner radius (4f, 8f, 12f, 16f)
 * - Proper elevation and shadow effects
 * - Ripple effects for touch feedback
 * - Color-coded status indicators
 * 
 * @author GroceryPlus Development Team
 * @version 1.0
 * @since 1.0
 */
public class UIComponents {

    /**
     * Create modern Material Design card with elevation and ripple effect.
     * 
     * This method applies Material Design 3 styling to card views with
     * proper elevation, corner radius, and ripple effects for touch feedback.
     * 
     * @param cardView The MaterialCardView to style
     * @param elevation The elevation value in pixels
     */
    public static void createModernCard(MaterialCardView cardView, int elevation) {
        cardView.setCardElevation(elevation);
        cardView.setRadius(16f);
        cardView.setUseCompatPadding(true);
        cardView.setRippleColorResource(R.color.md_theme_light_primary);
    }

    /**
     * Create gradient button with modern styling.
     * 
     * This method creates a button with gradient background, proper corner radius,
     * white text color, and ripple effect for Android Lollipop and above.
     * 
     * @param button The Button to style
     * @param startColor Starting color of gradient (left)
     * @param endColor Ending color of gradient (right)
     */
    public static void createGradientButton(Button button, int startColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(12f);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(Color.parseColor("#40000000")),
                    gradientDrawable,
                    null
            );
            button.setBackground(rippleDrawable);
        } else {
            button.setBackground(gradientDrawable);
        }
        
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
    }

    /**
     * Create modern Floating Action Button (FAB) with Material Design styling.
     * 
     * @param fab The FloatingActionButton to style
     */
    public static void createModernFAB(FloatingActionButton fab) {
        fab.setUseCompatPadding(true);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setElevation(8f);
    }

    /**
     * Create modern Material Design text input layout.
     * 
     * This method applies Material Design styling to text input layouts with
     * custom stroke color, hint text color, and stroke width.
     * 
     * @param textInputLayout The TextInputLayout to style
     */
    public static void createModernTextInput(TextInputLayout textInputLayout) {
        textInputLayout.setBoxStrokeColor(Color.parseColor("#6750A4"));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#49454F")));
        textInputLayout.setBoxStrokeWidth(2);
        // Note: setRadius is not available on TextInputLayout
    }

    /**
     * Create status badge with color coding based on status type.
     * 
     * This method creates a status badge with appropriate background color
     * based on the status value. Supports various order and delivery statuses.
     * 
     * Status Color Mapping:
     * - Green (#4CAF50): active, available, delivered
     * - Orange (#FF9800): pending, processing
     * - Red (#F44336): cancelled, unavailable, out of stock
     * - Gray (#9E9E9E): default/unknown
     * 
     * @param textView The TextView to style as badge
     * @param status The status string for color determination
     */
    public static void createStatusBadge(TextView textView, String status) {
        GradientDrawable badgeBackground = new GradientDrawable();
        badgeBackground.setShape(GradientDrawable.RECTANGLE);
        badgeBackground.setCornerRadius(12f);
        
        switch (status.toLowerCase()) {
            case "active":
            case "available":
            case "delivered":
                badgeBackground.setColor(Color.parseColor("#4CAF50"));
                textView.setTextColor(Color.WHITE);
                break;
            case "pending":
            case "processing":
                badgeBackground.setColor(Color.parseColor("#FF9800"));
                textView.setTextColor(Color.WHITE);
                break;
            case "cancelled":
            case "refunded":
            case "unavailable":
            case "out of stock":
                badgeBackground.setColor(Color.parseColor("#F44336"));
                textView.setTextColor(Color.WHITE);
                break;
            default:
                badgeBackground.setColor(Color.parseColor("#9E9E9E"));
                textView.setTextColor(Color.WHITE);
                break;
        }
        
        textView.setBackground(badgeBackground);
        textView.setPadding(16, 8, 16, 8);
        textView.setTextSize(12f);
    }

    /**
     * Create chip-style button with selection state styling.
     * 
     * This method creates a Material Design chip button with different colors
     * based on selection state. Commonly used for filters, categories, and options.
     * 
     * @param textView The TextView to style as chip
     * @param isSelected Whether the chip is currently selected
     */
    public static void createChipButton(TextView textView, boolean isSelected) {
        GradientDrawable chipBackground = new GradientDrawable();
        chipBackground.setShape(GradientDrawable.RECTANGLE);
        chipBackground.setCornerRadius(16f);
        
        if (isSelected) {
            chipBackground.setColor(Color.parseColor("#E8F5E8"));
            textView.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            chipBackground.setColor(Color.parseColor("#F5F5F5"));
            textView.setTextColor(Color.parseColor("#757575"));
        }
        
        chipBackground.setStroke(2, Color.parseColor("#4CAF50"));
        textView.setBackground(chipBackground);
        textView.setPadding(24, 12, 24, 12);
        textView.setTextSize(14f);
    }

    /**
     * Create shimmer loading effect for views.
     * 
     * This method applies a shimmer effect to indicate loading state,
     * commonly used for skeleton loading in product lists and cards.
     * 
     * @param view The View to apply shimmer effect to
     */
    public static void createShimmerEffect(View view) {
        GradientDrawable shimmerBackground = new GradientDrawable();
        shimmerBackground.setShape(GradientDrawable.RECTANGLE);
        shimmerBackground.setCornerRadius(8f);
        shimmerBackground.setColor(Color.parseColor("#E0E0E0"));
        
        view.setBackground(shimmerBackground);
        AnimationUtils.startShimmerAnimation(view);
    }

    /**
     * Create state view with color-coded backgrounds.
     * 
     * This method creates a state indicator with appropriate colors:
     * - Success (#E8F5E8): Green background, dark green text
     * - Error (#FFEBEE): Light red background, dark red text
     * - Warning (#FFF3E0): Light orange background, dark orange text
     * - Info (#E3F2FD): Light blue background, dark blue text
     * - Default (#F5F5F5): Gray background, dark gray text
     * 
     * @param textView The TextView to style
     * @param state The state string ("success", "error", "warning", "info")
     */
    public static void createStateView(TextView textView, String state) {
        GradientDrawable stateBackground = new GradientDrawable();
        stateBackground.setShape(GradientDrawable.RECTANGLE);
        stateBackground.setCornerRadius(8f);
        
        switch (state.toLowerCase()) {
            case "success":
                stateBackground.setColor(Color.parseColor("#E8F5E8"));
                textView.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "error":
                stateBackground.setColor(Color.parseColor("#FFEBEE"));
                textView.setTextColor(Color.parseColor("#C62828"));
                break;
            case "warning":
                stateBackground.setColor(Color.parseColor("#FFF3E0"));
                textView.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "info":
                stateBackground.setColor(Color.parseColor("#E3F2FD"));
                textView.setTextColor(Color.parseColor("#1565C0"));
                break;
            default:
                stateBackground.setColor(Color.parseColor("#F5F5F5"));
                textView.setTextColor(Color.parseColor("#757575"));
                break;
        }
        
        textView.setBackground(stateBackground);
        textView.setPadding(16, 12, 16, 12);
        textView.setTextSize(14f);
    }

    /**
     * Create price tag with Material Design styling.
     * 
     * @param textView The TextView to style as price tag
     */
    public static void createPriceTag(TextView textView) {
        GradientDrawable priceBackground = new GradientDrawable();
        priceBackground.setShape(GradientDrawable.RECTANGLE);
        priceBackground.setCornerRadius(4f);
        priceBackground.setColor(Color.parseColor("#4CAF50"));
        
        textView.setBackground(priceBackground);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextSize(12f);
    }

    /**
     * Create discount badge with percentage display.
     * 
     * @param textView The TextView to style as discount badge
     * @param discountPercentage The discount percentage to display
     */
    public static void createDiscountBadge(TextView textView, int discountPercentage) {
        GradientDrawable discountBackground = new GradientDrawable();
        discountBackground.setShape(GradientDrawable.RECTANGLE);
        discountBackground.setCornerRadius(4f);
        discountBackground.setColor(Color.parseColor("#FF5722"));
        
        textView.setBackground(discountBackground);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextSize(10f);
        textView.setText("-" + discountPercentage + "%");
    }

    /**
     * Create rating stars background with color based on rating value.
     * 
     * Rating Color Mapping:
     * - 4.0+: Gold (#FFF8E1) with orange text
     * - 3.0+: Light gold (#FFF3E0) with orange text
     * - Below 3.0: Light red (#FFEBEE) with dark red text
     * 
     * @param textView The TextView to style as rating display
     * @param rating The rating value (0.0 - 5.0)
     */
    public static void createRatingBackground(TextView textView, float rating) {
        GradientDrawable ratingBackground = new GradientDrawable();
        ratingBackground.setShape(GradientDrawable.RECTANGLE);
        ratingBackground.setCornerRadius(4f);
        
        if (rating >= 4.0) {
            ratingBackground.setColor(Color.parseColor("#FFF8E1"));
            textView.setTextColor(Color.parseColor("#FF8F00"));
        } else if (rating >= 3.0) {
            ratingBackground.setColor(Color.parseColor("#FFF3E0"));
            textView.setTextColor(Color.parseColor("#FF9800"));
        } else {
            ratingBackground.setColor(Color.parseColor("#FFEBEE"));
            textView.setTextColor(Color.parseColor("#F44336"));
        }
        
        textView.setBackground(ratingBackground);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextSize(12f);
    }

    /**
     * Create category chip with Material Design styling.
     * 
     * @param textView The TextView to style as category chip
     */
    public static void createCategoryChip(TextView textView) {
        GradientDrawable categoryBackground = new GradientDrawable();
        categoryBackground.setShape(GradientDrawable.RECTANGLE);
        categoryBackground.setCornerRadius(16f);
        categoryBackground.setColor(Color.parseColor("#E3F2FD"));
        categoryBackground.setStroke(1, Color.parseColor("#2196F3"));
        
        textView.setBackground(categoryBackground);
        textView.setTextColor(Color.parseColor("#1565C0"));
        textView.setPadding(16, 8, 16, 8);
        textView.setTextSize(12f);
    }

    /**
     * Create stock indicator with dynamic color and text based on stock level.
     * 
     * Stock Level Mapping:
     * - > 10: "In Stock" (Green #E8F5E8)
     * - 1-10: "Low Stock (X)" (Orange #FFF3E0)
     * - 0: "Out of Stock" (Red #FFEBEE)
     * 
     * @param textView The TextView to style as stock indicator
     * @param stock The current stock quantity
     */
    public static void createStockIndicator(TextView textView, int stock) {
        GradientDrawable stockBackground = new GradientDrawable();
        stockBackground.setShape(GradientDrawable.RECTANGLE);
        stockBackground.setCornerRadius(4f);
        
        if (stock > 10) {
            stockBackground.setColor(Color.parseColor("#E8F5E8"));
            textView.setTextColor(Color.parseColor("#2E7D32"));
            textView.setText("In Stock");
        } else if (stock > 0) {
            stockBackground.setColor(Color.parseColor("#FFF3E0"));
            textView.setTextColor(Color.parseColor("#F57C00"));
            textView.setText("Low Stock (" + stock + ")");
        } else {
            stockBackground.setColor(Color.parseColor("#FFEBEE"));
            textView.setTextColor(Color.parseColor("#C62828"));
            textView.setText("Out of Stock");
        }
        
        textView.setBackground(stockBackground);
        textView.setPadding(12, 6, 12, 6);
        textView.setTextSize(10f);
    }

    /**
     * Create delivery status indicator with color-coded status.
     * 
     * Delivery Status Color Mapping:
     * - Pending: Light orange (#FFF3E0) with dark orange text
     * - Confirmed: Light blue (#E3F2FD) with dark blue text
     * - Preparing: Light purple (#F3E5F5) with dark purple text
     * - Out for Delivery: Light green (#E8F5E8) with dark green text
     * - Delivered: Light green (#E8F5E8) with dark green text
     * - Cancelled: Light red (#FFEBEE) with dark red text
     * - Refunded: Light red (#FFEBEE) with dark red text
     * - Default: Light gray (#F5F5F5) with dark gray text
     * 
     * @param textView The TextView to style as delivery status
     * @param status The delivery status string
     */
    public static void createDeliveryStatusIndicator(TextView textView, String status) {
        GradientDrawable statusBackground = new GradientDrawable();
        statusBackground.setShape(GradientDrawable.RECTANGLE);
        statusBackground.setCornerRadius(8f);
        
        switch (status.toLowerCase()) {
            case "pending":
                statusBackground.setColor(Color.parseColor("#FFF3E0"));
                textView.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "confirmed":
                statusBackground.setColor(Color.parseColor("#E3F2FD"));
                textView.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "preparing":
                statusBackground.setColor(Color.parseColor("#F3E5F5"));
                textView.setTextColor(Color.parseColor("#7B1FA2"));
                break;
            case "out for delivery":
                statusBackground.setColor(Color.parseColor("#E8F5E8"));
                textView.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "delivered":
                statusBackground.setColor(Color.parseColor("#E8F5E8"));
                textView.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "cancelled":
            case "refunded":
                statusBackground.setColor(Color.parseColor("#FFEBEE"));
                textView.setTextColor(Color.parseColor("#C62828"));
                break;
            default:
                statusBackground.setColor(Color.parseColor("#F5F5F5"));
                textView.setTextColor(Color.parseColor("#757575"));
                break;
        }
        
        textView.setBackground(statusBackground);
        textView.setPadding(16, 8, 16, 8);
        textView.setTextSize(12f);
    }

    /**
     * Apply modern card shadow effect for Android Lollipop and above.
     * 
     * This method adds elevation and translation Z to create a modern
     * shadow effect for card views.
     * 
     * @param view The View to apply shadow effect to
     */
    public static void applyCardShadow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(8f);
            view.setTranslationZ(4f);
        }
    }

    /**
     * Apply gradient background to any view.
     * 
     * @param view The View to apply gradient background to
     * @param startColor Starting color of gradient
     * @param endColor Ending color of gradient
     */
    public static void applyGradientBackground(View view, int startColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(16f);
        view.setBackground(gradientDrawable);
    }

    /**
     * Create bordered text view with custom border styling.
     * 
     * @param textView The TextView to apply border to
     * @param borderColor The color of the border
     * @param borderWidth The width of the border in pixels
     */
    public static void createBorderedTextView(TextView textView, int borderColor, int borderWidth) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setCornerRadius(8f);
        borderDrawable.setStroke(borderWidth, borderColor);
        borderDrawable.setColor(Color.TRANSPARENT);
        
        textView.setBackground(borderDrawable);
        textView.setPadding(12, 8, 12, 8);
    }
}
