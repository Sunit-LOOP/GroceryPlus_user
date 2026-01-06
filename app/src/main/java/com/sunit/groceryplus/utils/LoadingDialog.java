package com.sunit.groceryplus.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.sunit.groceryplus.R;

/** Utility class providing a standardized loading indicator for long-running operations. */
public class LoadingDialog {

    // Infrastructure
    private Activity activity;
    private AlertDialog dialog;

    /** Initializes the loading dialog with the host activity's context. */
    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    /** Inflates and displays a non-cancelable loading dialog on the current screen. */
    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_loading_state, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /** Safely dismisses the loading dialog if it is currently visible. */
    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
