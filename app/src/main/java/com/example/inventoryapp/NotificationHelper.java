package com.example.inventoryapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class NotificationHelper {
    private static final String CHANNEL_ID = "LowInventoryChannel";
    private static final String CHANNEL_NAME = "Low Inventory Channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    public static void showLowInventoryNotification(Context context, String message) {
        if (checkNotificationPermission(context)) {
            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Low Inventory")
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            requestNotificationPermission(context);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Low Inventory Notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static boolean checkNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification permission is only required on Android Oreo (API 26) and above
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Return true for pre-Oreo devices
    }

    public static void requestNotificationPermission(Context context) {
        if (context instanceof FragmentActivity) {
            Fragment permissionFragment = new NotificationPermissionFragment();
            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .add(permissionFragment, "NotificationPermissionFragment")
                    .commit();
        }
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            Fragment permissionFragment = new NotificationPermissionFragment();
            fragmentManager.beginTransaction()
                    .add(permissionFragment, "NotificationPermissionFragment")
                    .commit();
        }
    }

    public static void onRequestPermissionsResult(int requestCode, int[] grantResults, Context context) {
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the notification
                showLowInventoryNotification(context, "Low inventory notification");
            }
        }
    }

    public static class NotificationPermissionFragment extends Fragment {
        public NotificationPermissionFragment() {}

        @Override
        public void onResume() {
            super.onResume();
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }
}
