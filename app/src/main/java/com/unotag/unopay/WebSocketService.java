package com.unotag.unopay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    private static final String CHANNEL_ID = "WebSocketChannel";
    private static int notificationId = 0; // Static counter for unique notification IDs
    private String lastMessage = ""; // Track the last received message
    private OkHttpClient client;
    private WebSocket webSocket;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        createNotificationChannel();
        startForeground(1, getServiceNotification("UNOPAY WELCOMES YOU")); // Run as foreground service
        startWebSocket();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    private void startWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://unotag.biz/api/")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "WebSocket Connected: " + response.message());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d(TAG, "Received Message: " + text);

                // Ignore duplicate messages
                if (text.equals(lastMessage)) {
                    Log.d(TAG, "Duplicate message ignored");
                    return;
                }
                lastMessage = text;

                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String title = jsonObject.optString("title", "Notification");
                    String body = jsonObject.optString("body", "You have a new message!");

                    showNotification(title, body);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing WebSocket message", e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "WebSocket Connection Failed", t);

                // Attempt to reconnect after a delay
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.d(TAG, "Attempting to reconnect...");
                    startWebSocket();
                }, 5000); // Reconnect after 5 seconds
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "WebSocket Closed: Code = " + code + ", Reason = " + reason);
            }
        });
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "WebSocket Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.uno)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Use a unique notification ID and reset if necessary
        if (notificationId == Integer.MAX_VALUE) {
            notificationId = 0; // Reset to avoid overflow
        }
        notificationManager.notify(notificationId++, builder.build());
        Log.d(TAG, "Notification displayed: Title = " + title + ", Body = " + body);
    }

    private Notification getServiceNotification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.uno)
                .setContentTitle("UNO PAY")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WebSocket Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service Destroyed");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not using bound services in this case
    }
}