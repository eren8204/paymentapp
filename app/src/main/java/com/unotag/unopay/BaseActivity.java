package com.unotag.unopay;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements NetworkMonitor.NetworkListener {
    private AlertDialog networkDialog;
    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkMonitor = NetworkMonitor.getInstance(this);
        networkMonitor.startMonitoring(this);

        NetworkChangeReceiver.setNetworkChangeListener(isConnected -> {
            if (!isConnected) {
                new Handler(Looper.getMainLooper()).post(this::showNoInternetDialog);
            } else {
                new Handler(Looper.getMainLooper()).post(this::dismissNoInternetDialog);
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
                new Handler(Looper.getMainLooper()).post(this::showNoInternetDialog);
            } else {
                new Handler(Looper.getMainLooper()).post(this::dismissNoInternetDialog);
            }
        });
    }

    private void showNoInternetDialog() {
        if (!isAppInForeground() || isFinishing() || isDestroyed()) {
            return;
        }
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
                    new Handler(Looper.getMainLooper()).post(this::dismissNoInternetDialog);
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
        boolean isConnected = NetworkMonitor.isConnected(this);
        if (!isConnected) {
            new Handler(Looper.getMainLooper()).post(this::showNoInternetDialog);
        } else {
            new Handler(Looper.getMainLooper()).post(this::dismissNoInternetDialog);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //
    }

    public boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();

        if (processes == null) return false;

        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.processName.equals(getPackageName()) &&
                    process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
