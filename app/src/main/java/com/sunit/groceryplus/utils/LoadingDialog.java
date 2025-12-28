package com.sunit.groceryplus.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.sunit.groceryplus.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_loading_state, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
