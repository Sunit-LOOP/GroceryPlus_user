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

public class UIComponents {

    // Create modern card with elevation and ripple effect
    public static void createModernCard(MaterialCardView cardView, int elevation) {
        cardView.setCardElevation(elevation);
        cardView.setRadius(16f);
        cardView.setUseCompatPadding(true);
        cardView.setRippleColorResource(R.color.md_theme_light_primary);
    }

    // Create gradient button
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

    // Create floating action button with custom background
    public static void createModernFAB(FloatingActionButton fab) {
        fab.setUseCompatPadding(true);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.setElevation(8f);
    }

    // Create modern text input layout
    public static void createModernTextInput(TextInputLayout textInputLayout) {
        textInputLayout.setBoxStrokeColor(Color.parseColor("#6750A4"));
        textInputLayout.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#49454F")));
        textInputLayout.setBoxStrokeWidth(2);
        // setRadius is not available on TextInputLayout
    }

    // Create status badge
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

    // Create chip-style button
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

    // Create loading shimmer effect
    public static void createShimmerEffect(View view) {
        GradientDrawable shimmerBackground = new GradientDrawable();
        shimmerBackground.setShape(GradientDrawable.RECTANGLE);
        shimmerBackground.setCornerRadius(8f);
        shimmerBackground.setColor(Color.parseColor("#E0E0E0"));
        
        view.setBackground(shimmerBackground);
        AnimationUtils.startShimmerAnimation(view);
    }

    // Create success/error state view
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

    // Create price tag style
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

    // Create discount badge
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

    // Create rating stars background
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

    // Create category chip
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

    // Create stock indicator
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

    // Create delivery status indicator
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

    // Apply modern card style with shadow
    public static void applyCardShadow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(8f);
            view.setTranslationZ(4f);
        }
    }

    // Create gradient background for views
    public static void applyGradientBackground(View view, int startColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(16f);
        view.setBackground(gradientDrawable);
    }

    // Create bordered text view
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
