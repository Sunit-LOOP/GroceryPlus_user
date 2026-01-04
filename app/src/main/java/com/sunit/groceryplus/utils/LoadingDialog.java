package com.sunit.groceryplus.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.sunit.groceryplus.R;

/**
 * LoadingDialog - A utility class to display a loading dialog.
 * 
 * This class provides a standardized loading indicator that can be shown
 * during long-running operations (network requests, database operations).
 * It uses a custom layout for a consistent look and feel.
 */
public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    /**
     * Constructor.
     * 
     * @param activity The activity where the dialog will be shown.
     */
    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    /**
     * Shows the loading dialog.
     * 
     * This method inflates the custom layout, sets it to an AlertDialog,
     * and shows it. The dialog is not cancelable by touching outside.
     */
    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_loading_state, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * Dismisses the loading dialog.
     * 
     * Safely dismisses the dialog if it is currently showing.
     */
    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
