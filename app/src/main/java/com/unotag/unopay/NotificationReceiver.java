package com.unotag.unopay;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("arsh", "Broadcast received. Preparing to send notification...");

        // Check if notifications are enabled
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!areNotificationsEnabled(notificationManager, context)) {
            Log.e("arsh", "Notifications are not enabled. Aborting notification.");
            return;
        }

        // Create Notification Channel (for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for daily 8 AM notification");
            notificationManager.createNotificationChannel(channel);
        }

        // Create Intent to open MainActivity when the notification is clicked
        Intent notificationIntent = new Intent(context, Login.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("UNO PAY")
                .setContentText("Happy member dear member")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
            Log.d("arsh", "Notification sent successfully.");
        } else {
            Log.e("arsh", "NotificationManager is null. Notification could not be sent.");
        }
    }

    private boolean areNotificationsEnabled(NotificationManager notificationManager, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationManager.areNotificationsEnabled()) {
                Log.e("arsh", "Notifications are disabled for this app.");
                return false;
            }

            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel != null && channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Log.e("arsh", "Notification channel is disabled.");
                return false;
            }
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }

        return true;
    }

    public static boolean isExactAlarmPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Permissions not required for versions below Android 12 (S)
    }
}
