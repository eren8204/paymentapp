package com.unotag.unopay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static NetworkChangeListener listener;

    public static void setNetworkChangeListener(NetworkChangeListener networkChangeListener) {
        listener = networkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            listener.onNetworkChange(NetworkMonitor.isConnected(context));
        }
    }

    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }
}
