package com.unotag.unopay;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements NetworkMonitor.NetworkListener {
    private AlertDialog networkDialog;
    private NetworkMonitor networkMonitor;
    private NetworkChangeReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkMonitor = NetworkMonitor.getInstance(this);
        networkMonitor.startMonitoring(this);
        NetworkChangeReceiver.setNetworkChangeListener(isConnected -> {
            if (!isConnected) {
                showNoInternetDialog();
            }
            else {
                dismissNoInternetDialog();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        // Initialize receiver if null
        if (networkReceiver == null) {
            networkReceiver = new NetworkChangeReceiver();
        }

        // Register receiver manually
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Manually check connection on resume
        boolean isConnected = NetworkMonitor.isConnected(this);
        if (!isConnected) {
            showNoInternetDialog();
        }
        else {
            dismissNoInternetDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister receiver to prevent leaks
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver);
        }
    }

}
