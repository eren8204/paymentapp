package com.unotag.unopay;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

public class NetworkMonitor extends ConnectivityManager.NetworkCallback {
    private static NetworkMonitor instance;
    private static NetworkListener listener;
    private final ConnectivityManager connectivityManager;

    private NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkMonitor getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkMonitor(context);
        }
        return instance;
    }

    public void startMonitoring(NetworkListener networkListener) {
        listener = networkListener;
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, this);
    }

    public void stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        if (listener != null) {
            listener.onNetworkChanged(true);
        }
    }

    @Override
    public void onLost(@NonNull Network network) {
        if (listener != null) {
            listener.onNetworkChanged(false);
        }
    }

    public interface NetworkListener {
        void onNetworkChanged(boolean isConnected);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }
}
