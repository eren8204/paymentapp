package com.unotag.unopay;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements NetworkMonitor.NetworkListener {
    private AlertDialog networkDialog;
    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkMonitor = NetworkMonitor.getInstance(this);
        networkMonitor.startMonitoring(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkMonitor.stopMonitoring();
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        runOnUiThread(() -> {
            if (!isConnected) {
                showNoInternetDialog();
            } else {
                dismissNoInternetDialog();
            }
        });
    }

    private void showNoInternetDialog() {
        if (networkDialog == null || !networkDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again.")
                    .setCancelable(false);

            builder.setPositiveButton("Retry", null);

            networkDialog = builder.create();
            networkDialog.show();

            networkDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (NetworkMonitor.isConnected(this)) {
                    dismissNoInternetDialog();
                } else {
                    Toast.makeText(this, "Still no internet. Please check your connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void dismissNoInternetDialog() {
        if (networkDialog != null && networkDialog.isShowing()) {
            networkDialog.dismiss();
        }
    }
}
